package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@Service
public class EstadisticasPdfService {

    public ByteArrayInputStream generarReporteEstadisticas(Map<EstadoAsistencia, Long> estadisticas) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 🔹 Logo desde resources/static/img
            InputStream logoStream = getClass().getResourceAsStream("/static/img/logocheckin.png");
            if (logoStream != null) {
                ImageData imageData = ImageDataFactory.create(logoStream.readAllBytes());
                Image logo = new Image(imageData).scaleToFit(80, 80);
                logo.setFixedPosition(50, 750); // posición fija
                document.add(logo);
            }

            // 🔹 Título
            Paragraph titulo = new Paragraph("📊 Reporte de Estadísticas de Asistencia")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // 🔹 Tabla de estadísticas
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{50, 25, 25}))
                    .useAllAvailableWidth();

            tabla.addHeaderCell(new Cell().add(new Paragraph("Estado"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Cantidad"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Porcentaje"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold());

            long total = estadisticas.values().stream().mapToLong(Long::longValue).sum();

            for (Map.Entry<EstadoAsistencia, Long> entry : estadisticas.entrySet()) {
                long cantidad = entry.getValue();
                double porcentaje = total > 0 ? (cantidad * 100.0 / total) : 0.0;

                tabla.addCell(entry.getKey().name());
                tabla.addCell(String.valueOf(cantidad));
                tabla.addCell(String.format("%.2f%%", porcentaje));
            }

            document.add(tabla);

            // 🔹 Resumen final
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total registros: " + total).setBold());
            document.add(new Paragraph("Generado por: Sistema Check-In")
                    .setItalic()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}
