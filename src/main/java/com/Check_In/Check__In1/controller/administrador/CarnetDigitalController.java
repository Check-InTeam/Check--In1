package com.Check_In.Check__In1.controller.administrador;

import com.Check_In.Check__In1.entity.CarnetDigital;
import com.Check_In.Check__In1.repository.CarnetDigitalRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/administrador/carnet_digital")
public class CarnetDigitalController {

    private static final String UPLOAD_DIR = "uploads/carnet/";

    @Autowired
    private CarnetDigitalRepository carnetDigitalRepository;

    @Autowired
    private UserRepository userRepository;

    // ---------------- LISTAR CARNETS ----------------
    @GetMapping
    public String index(Model model) {
        List<CarnetDigital> carnets = carnetDigitalRepository.findAll();
        model.addAttribute("carnets", carnets);
        return "administrador/carnet_digital/index";
    }

    // ---------------- FORMULARIO EDITAR ----------------
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        CarnetDigital carnet = carnetDigitalRepository.findById(id).orElse(null);
        if (carnet == null) {
            redirectAttributes.addFlashAttribute("error", "Carnet no encontrado");
            return "redirect:/administrador/carnet_digital";
        }
        model.addAttribute("carnet", carnet);
        return "administrador/carnet_digital/edit";
    }

    // ---------------- ACTUALIZAR CARNET ----------------
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("carnet") CarnetDigital updatedCarnet,
                         @RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes) {

        CarnetDigital carnet = carnetDigitalRepository.findById(id).orElse(null);
        if (carnet == null) {
            redirectAttributes.addFlashAttribute("error", "Carnet no encontrado");
            return "redirect:/administrador/carnet_digital";
        }

        // Actualizar campos
        carnet.setNombreCompleto(updatedCarnet.getNombreCompleto());
        carnet.setNumeroDeIdentificacion(updatedCarnet.getNumeroDeIdentificacion());
        carnet.setFicha(updatedCarnet.getFicha());
        carnet.setPrograma(updatedCarnet.getPrograma());
        carnet.setJornada(updatedCarnet.getJornada());

        // Subir nueva foto si hay archivo
        if (!file.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());

                // Crear carpeta si no existe
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Guardar archivo
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                carnet.setFoto(fileName); // Guardar solo el nombre del archivo

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error al subir la foto");
                return "redirect:/administrador/carnet_digital/edit/" + id;
            }
        }

        carnetDigitalRepository.save(carnet);
        redirectAttributes.addFlashAttribute("message", "Carnet actualizado correctamente");
        return "redirect:/administrador/carnet_digital";
    }

    // ---------------- ELIMINAR CARNET ----------------
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        carnetDigitalRepository.deleteById(id);
        model.addAttribute("mensaje", "Carnet eliminado correctamente.");
        return "/administrador/carnet_digital/delete";
    }

    // ---------------- MOSTRAR FOTO ----------------
    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getFoto(@PathVariable("id") int id) throws IOException {
        CarnetDigital carnet = carnetDigitalRepository.findById(id).orElse(null);
        if (carnet == null || carnet.getFoto() == null) {
            return ResponseEntity.notFound().build();
        }

        Path fotoPath = Paths.get(UPLOAD_DIR).resolve(carnet.getFoto());
        if (!Files.exists(fotoPath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fotoBytes = Files.readAllBytes(fotoPath);
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(fotoBytes);
    }

    // ---------------- GENERAR QR ----------------
    @GetMapping("/qr/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getQRCode(@PathVariable("id") int id) throws IOException, WriterException {
        CarnetDigital carnet = carnetDigitalRepository.findById(id).orElse(null);
        if (carnet == null) {
            return ResponseEntity.notFound().build();
        }

        String qrContent = "USER-" + carnet.getId() + "-" + carnet.getNombreCompleto();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(pngData);
    }

    // ---------------- DESCARGAR CARNET PDF ----------------
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCarnet(@PathVariable int id) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            CarnetDigital carnet = carnetDigitalRepository.findById(id).orElse(null);
            if (carnet == null) return ResponseEntity.notFound().build();

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDoc);

            // Página 1
            PdfPage page1 = pdfDoc.addNewPage();
            PdfCanvas canvas1 = new PdfCanvas(page1);
            ImageData fondo1 = ImageDataFactory.create(getClass().getResource("/static/img/DiseñoCarnet_1.jpg"));
            canvas1.addImageFittedIntoRectangle(fondo1, page1.getPageSize(), false);

            // Foto
            if (carnet.getFoto() != null) {
                Path fotoPath = Paths.get(UPLOAD_DIR).resolve(carnet.getFoto());
                if (Files.exists(fotoPath)) {
                    ImageData fotoData = ImageDataFactory.create(Files.readAllBytes(fotoPath));
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

            // Texto Carnet
            PdfFont font = PdfFontFactory.createFont(getClass().getResource("/static/fonts/BrittanySignature.ttf").toString(), PdfEncodings.IDENTITY_H);
            PdfFont fontCaviar = PdfFontFactory.createFont(getClass().getResource("/static/fonts/CaviarDreams.ttf").toString(), PdfEncodings.IDENTITY_H);

            String[] lineas = {
                    " " + carnet.getNombreCompleto(),
                    "  " + carnet.getNumeroDeIdentificacion(),
                    "  " + carnet.getPrograma(),
                    "  " + carnet.getFicha()
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
                Paragraph p = new Paragraph(lineas[i])
                        .setFont(fuente)
                        .setFontColor(colors[i])
                        .setFontSize(fontSize)
                        .setTextAlignment(TextAlignment.CENTER);
                document.showTextAligned(p, page1.getPageSize().getWidth() / 2, textY, TextAlignment.CENTER);
                textY -= (i == 0 ? 50 : i == 1 ? 55 : 45);
            }

            // Página 2 - QR
            PdfPage page2 = pdfDoc.addNewPage();
            PdfCanvas canvas2 = new PdfCanvas(page2);
            ImageData fondo2 = ImageDataFactory.create(getClass().getResource("/static/img/DiseñoCarnet2.jpg"));
            canvas2.addImageFittedIntoRectangle(fondo2, page2.getPageSize(), false);

            String qrContent = "USER-" + carnet.getId() + "-" + carnet.getNombreCompleto();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 450, 450);
            ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOutputStream);
            ImageData qrImageData = ImageDataFactory.create(qrOutputStream.toByteArray());
            Image qrImage = new Image(qrImageData);
            float qrWidth = 320;
            float qrHeight = 320;
            float qrX = (page2.getPageSize().getWidth() - qrWidth) / 2 + 30;
            float qrY = (page2.getPageSize().getHeight() - qrHeight) / 2;
            qrImage.setWidth(qrWidth);
            qrImage.setHeight(qrHeight);
            qrImage.setFixedPosition(2, qrX, qrY);
            document.add(qrImage);

            // Texto adicional
            PdfFont fontC = PdfFontFactory.createFont(getClass().getResource("/static/fonts/CaviarDreams.ttf").toString(), PdfEncodings.IDENTITY_H);
            PdfFont fontB = PdfFontFactory.createFont(getClass().getResource("/static/fonts/BrittanySignature.ttf").toString(), PdfEncodings.IDENTITY_H);
            String jornada = " " + carnet.getJornada();
            String anioActual = " " + java.time.LocalDate.now().getYear();
            float textPosY = qrY - 110;
            float pageWidth = page2.getPageSize().getWidth();
            document.showTextAligned(new Paragraph(jornada).setFont(fontB).setFontSize(32).setFontColor(new DeviceRgb(2, 76, 2)),
                    pageWidth / 2, textPosY, 2, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
            document.showTextAligned(new Paragraph(anioActual).setFont(fontC).setFontSize(32).setFontColor(new DeviceRgb(75, 0, 130)),
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
}
