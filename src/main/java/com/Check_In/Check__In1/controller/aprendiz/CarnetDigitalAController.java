package com.Check_In.Check__In1.controller.aprendiz;

import com.Check_In.Check__In1.entity.CarnetDigital;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.CarnetDigitalService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Controller
@RequestMapping("/aprendiz/carnet_digital")
public class
CarnetDigitalAController {

    @Autowired
    private CarnetDigitalService carnetDigitalService;

    @Autowired
    private UserRepository userRepository;

    // Mostrar todos los carnets
    @GetMapping
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("carnet", carnetDigitalService.getCarnetByUser(user).orElse(null));

        return "aprendiz/carnet_digital/index";
    }

    // Mostrar formulario para crear carnet
    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        // Si ya tiene carnet, lo lleva al index
        if (carnetDigitalService.getCarnetByUser(user).isPresent()) {
            return "redirect:/aprendiz/carnet_digital";
        }

        model.addAttribute("carnet", new CarnetDigital());
        return "aprendiz/carnet_digital/create";
    }

    // Guardar carnet nuevo
    @PostMapping("/guardar")
    public String saveCarnet(@ModelAttribute CarnetDigital carnet,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session) throws IOException {

        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        // Verificar rol
        String rol = user.getRole().getNombre();// Ajusta si tu campo rol se llama diferente

        // ✔ SOLO EL ADMIN puede subir foto
        if (file != null && !file.isEmpty() && rol.equalsIgnoreCase("ADMIN")) {

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File uploadDir = new File("uploads/carnet/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            try (FileOutputStream fos = new FileOutputStream("uploads/carnet/" + filename)) {
                fos.write(file.getBytes());
            }

            carnet.setFoto(filename);
        }

        // Si no es admin, se ignora completamente cualquier foto enviada
        carnet.setUser(user);
        carnetDigitalService.saveCarnet(carnet);

        return "redirect:/aprendiz/carnet_digital";
    }



    // Mostrar foto
    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getFoto(@PathVariable int id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return ResponseEntity.status(401).build();

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) {
            return ResponseEntity.notFound().build();
        }

        CarnetDigital carnet = carnetOpt.get();
        if (carnet.getFoto() == null) return ResponseEntity.notFound().build();

        File file = new File("uploads/carnet/" + carnet.getFoto());
        if (!file.exists()) return ResponseEntity.notFound().build();

        byte[] fotoBytes = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(fotoBytes);
    }

    // Obtener QR
    @GetMapping("/qr/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getQr(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) {
            return ResponseEntity.notFound().build();
        }

        CarnetDigital carnet = carnetOpt.get();
        try {
            String qrContent = "USER-" + carnet.getId() + "-" + carnet.getNombreCompleto();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(pngData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Descargar PDF del carnet
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadCarnet(@PathVariable int id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return ResponseEntity.status(401).build();

        CarnetDigital carnet = carnetDigitalService.getCarnetDigitalById(id).orElse(null);
        if (carnet == null || carnet.getUser().getId() != user.getId()) {
            return ResponseEntity.status(403).build();
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDoc);

            // ---------------- PÁGINA 1 ----------------
            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas1 = new PdfCanvas(page1);
            ImageData fondo1 = ImageDataFactory.create(getClass().getResource("/static/img/DiseñoCarnet_1.jpg"));
            canvas1.addImageFittedIntoRectangle(fondo1, page1.getPageSize(), false);

            // FOTO desde archivo
            if (carnet.getFoto() != null) {
                File file = new File("uploads/carnet/" + carnet.getFoto());
                if (file.exists()) {
                    ImageData fotoData = ImageDataFactory.create(Files.readAllBytes(file.toPath()));
                    Image foto = new Image(fotoData);
                    float fotoWidth = 205;
                    float fotoHeight = 220;
                    float offsetX = 40;
                    float fotoX = (page1.getPageSize().getWidth() - fotoWidth) / 2 + offsetX;
                    float fotoY = page1.getPageSize().getHeight() - 500;
                    foto.setFixedPosition(fotoX, fotoY);
                    foto.setWidth(fotoWidth);
                    foto.setHeight(fotoHeight);
                    document.add(foto);
                }
            }

            // TEXTO
            PdfFont font = PdfFontFactory.createFont(getClass().getResource("/static/fonts/BrittanySignature.ttf").toString(), PdfEncodings.IDENTITY_H);
            PdfFont fontCaviar = PdfFontFactory.createFont(getClass().getResource("/static/fonts/CaviarDreams.ttf").toString(), PdfEncodings.IDENTITY_H);

            String[] lineas = {
                    " " + carnet.getNombreCompleto(),
                    "  " + carnet.getNumeroDeIdentificacion(),
                    "  " + carnet.getPrograma(),
                    "  " + carnet.getFicha(),
            };

            DeviceRgb[] colors = {
                    new DeviceRgb(2, 76, 2),
                    new DeviceRgb(2, 20, 92),
                    new DeviceRgb(0, 0, 0),
                    new DeviceRgb(19, 0, 64)
            };

            float textY = 190;
            for (int i = 0; i < lineas.length; i++) {
                PdfFont fuente = (i == 1 || i == 3) ? fontCaviar : font;
                int fontSize = (i == 1 || i == 3) ? 24 : 26;
                Paragraph p = new Paragraph(lineas[i]).setFont(fuente).setFontColor(colors[i]).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER);
                document.showTextAligned(p, page1.getPageSize().getWidth() / 2, textY, TextAlignment.CENTER);
                textY -= (i == 0) ? 50 : (i == 1) ? 55 : 45;
            }

            // ---------------- PÁGINA 2 ----------------
            PdfPage page2 = pdfDoc.addNewPage();
            PdfCanvas canvas2 = new PdfCanvas(page2);
            ImageData fondo2 = ImageDataFactory.create(getClass().getResource("/static/img/DiseñoCarnet2.jpg"));
            canvas2.addImageFittedIntoRectangle(fondo2, page2.getPageSize(), false);

            // QR
            String qrContent = "USER-" + carnet.getId() + "-" + carnet.getNombreCompleto();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 450, 450);
            ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOutputStream);
            ImageData qrImageData = ImageDataFactory.create(qrOutputStream.toByteArray());
            Image qrImage = new Image(qrImageData);
            float qrWidth = 320, qrHeight = 320;
            float qrX = (page2.getPageSize().getWidth() - qrWidth) / 2 + 30;
            float qrY = (page2.getPageSize().getHeight() - qrHeight) / 2;
            qrImage.setWidth(qrWidth);
            qrImage.setHeight(qrHeight);
            qrImage.setFixedPosition(2, qrX, qrY);
            document.add(qrImage);

            // TEXTO EXTRA
            PdfFont fontB = PdfFontFactory.createFont(getClass().getResource("/static/fonts/BrittanySignature.ttf").toString(), PdfEncodings.IDENTITY_H);
            String jornada = " " + carnet.getJornada();
            String anioActual = " " + java.time.LocalDate.now().getYear();
            float textPosY = qrY - 110, pageWidth = page2.getPageSize().getWidth();
            document.showTextAligned(new Paragraph(jornada).setFont(fontB).setFontSize(32).setFontColor(new DeviceRgb(2, 76, 2)),
                    pageWidth / 2, textPosY, 2, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
            document.showTextAligned(new Paragraph(anioActual).setFont(fontB).setFontSize(32).setFontColor(new DeviceRgb(75, 0, 130)),
                    pageWidth / 2, textPosY - 50, 2, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);

            pdfDoc.close();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=carnet.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Editar carnet
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) {
            return "redirect:/aprendiz/carnet_digital";
        }

        model.addAttribute("carnet", carnetOpt.get());
        return "aprendiz/carnet_digital/edit";
    }

    // Actualizar carnet
    @PostMapping("/update/{id}")
    public String updateCarnet(@PathVariable int id,
                               @ModelAttribute CarnetDigital updatedData,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) {
            return "redirect:/aprendiz/carnet_digital";
        }

        CarnetDigital carnet = carnetOpt.get();
        carnet.setNombreCompleto(updatedData.getNombreCompleto());
        carnet.setNumeroDeIdentificacion(updatedData.getNumeroDeIdentificacion());
        carnet.setFicha(updatedData.getFicha());
        carnet.setPrograma(updatedData.getPrograma());
        carnet.setJornada(updatedData.getJornada());

        // Guardar nueva foto si existe
        if (file != null && !file.isEmpty()) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File uploadDir = new File("uploads/carnet/");
            if (!uploadDir.exists()) uploadDir.mkdirs();
            try (FileOutputStream fos = new FileOutputStream("uploads/carnet/" + filename)) {
                fos.write(file.getBytes());
            }
            carnet.setFoto(filename);
        }

        carnetDigitalService.saveCarnet(carnet);
        return "redirect:/aprendiz/carnet_digital";
    }
}
