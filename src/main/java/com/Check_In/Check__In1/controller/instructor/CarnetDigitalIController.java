package com.Check_In.Check__In1.controller.instructor;

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
@RequestMapping("/instructor/carnet_digital")
public class CarnetDigitalIController {

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

        return "instructor/carnet_digital/index";
    }

    // Formulario para crear carnet
    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        if (carnetDigitalService.getCarnetByUser(user).isPresent()) {
            return "redirect:/instructor/carnet_digital";
        }

        model.addAttribute("carnet", new CarnetDigital());
        return "instructor/carnet_digital/create";
    }

    // Guardar carnet nuevo con foto
    @PostMapping("/guardar")
    public String saveCarnet(@ModelAttribute CarnetDigital carnet,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        String rol = user.getRole().getNombre();

        // Guardar foto en carpeta
        if (file != null && !file.isEmpty() && rol.equalsIgnoreCase("ADMINISTRADOR")) {

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File uploadDir = new File("uploads/carnet/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            try (FileOutputStream fos = new FileOutputStream("uploads/carnet/" + filename)) {
                fos.write(file.getBytes());
            }
            carnet.setFoto(filename); // ahora foto es String
        }

        carnet.setUser(user);
        carnetDigitalService.saveCarnet(carnet);
        return "redirect:/instructor/carnet_digital";
    }

    // Mostrar foto desde archivo
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

    // QR
    @GetMapping("/qr/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getQr(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return ResponseEntity.status(401).build();

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) return ResponseEntity.notFound().build();

        CarnetDigital carnet = carnetOpt.get();
        try {
            String qrContent = "USER-" + carnet.getId() + "-" + carnet.getNombreCompleto();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(pngOutputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Descargar PDF
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadCarnet(@PathVariable int id, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return ResponseEntity.status(401).build();

        CarnetDigital carnet = carnetDigitalService.getCarnetDigitalById(id).orElse(null);
        if (carnet == null || carnet.getUser().getId() != user.getId()) return ResponseEntity.status(403).build();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDoc);

            // Página 1
            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas1 = new PdfCanvas(page1);
            ImageData fondo1 = ImageDataFactory.create(getClass().getResource("/static/img/DiseñoCarnet_1.jpg"));
            canvas1.addImageFittedIntoRectangle(fondo1, page1.getPageSize(), false);

            if (carnet.getFoto() != null) {
                File file = new File("uploads/carnet/" + carnet.getFoto());
                if (file.exists()) {
                    ImageData fotoData = ImageDataFactory.create(Files.readAllBytes(file.toPath()));
                    Image foto = new Image(fotoData);
                    foto.setFixedPosition(40, page1.getPageSize().getHeight() - 500);
                    foto.setWidth(205);
                    foto.setHeight(220);
                    document.add(foto);
                }
            }

            // Resto del PDF igual que antes (texto, página 2, QR)...
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
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) return "redirect:/instructor/carnet_digital";

        model.addAttribute("carnet", carnetOpt.get());
        return "instructor/carnet_digital/edit";
    }

    // Actualizar carnet con nueva foto
    @PostMapping("/update/{id}")
    public String updateCarnet(@PathVariable int id,
                               @ModelAttribute CarnetDigital updatedData,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               HttpSession session) throws IOException {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        Optional<CarnetDigital> carnetOpt = carnetDigitalService.getCarnetDigitalById(id);
        if (carnetOpt.isEmpty() || carnetOpt.get().getUser().getId() != user.getId()) return "redirect:/instructor/carnet_digital";

        CarnetDigital carnet = carnetOpt.get();
        carnet.setNombreCompleto(updatedData.getNombreCompleto());
        carnet.setNumeroDeIdentificacion(updatedData.getNumeroDeIdentificacion());
        carnet.setFicha(updatedData.getFicha());
        carnet.setPrograma(updatedData.getPrograma());
        carnet.setJornada(updatedData.getJornada());

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
        return "redirect:/instructor/carnet_digital";
    }
}
