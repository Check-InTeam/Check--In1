package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.Asistencia;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarReportePdf(Reportes reporte) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ================================
            // 🟦 1. Encabezado con Logo + Título
            // ================================

            Table headerTable = new Table(2);
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Logo a la derecha
            InputStream logoStream = getClass().getResourceAsStream("/static/img/logocheckin.png");
            Cell logoCell;

            if (logoStream != null) {
                ImageData imageData = ImageDataFactory.create(logoStream.readAllBytes());
                Image logo = new Image(imageData);
                logo.setWidth(80);

                logoCell = new Cell().add(logo)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT);
            } else {
                logoCell = new Cell().add(new Paragraph(""))
                        .setBorder(Border.NO_BORDER);
            }

            // Título a la izquierda del logo
            Cell titleCell = new Cell()
                    .add(new Paragraph("Reporte del Aprendiz")
                            .setFontSize(22)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT))
                    .setBorder(Border.NO_BORDER);

            headerTable.addCell(titleCell);
            headerTable.addCell(logoCell);
            document.add(headerTable);

            document.add(new Paragraph("\n")); // espacio


            // ================================
            // 🟦 2. Datos del reporte
            // ================================

            document.add(new Paragraph("Nombre: " + reporte.getUser().getNombre())
                    .setFontSize(12)
                    .setMarginBottom(5));

            document.add(new Paragraph("Fecha de creación: " + reporte.getFechaCreacion())
                    .setFontSize(12)
                    .setMarginBottom(5));

            document.add(new Paragraph("Tipo de reporte: " + reporte.getTipo().name())
                    .setFontSize(12)
                    .setMarginBottom(5));

            document.add(new Paragraph("Título: " + reporte.getTitulo())
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(8));

            // Descripción en formato correo 📩
            document.add(new Paragraph("Descripción:\n" + reporte.getDescripcion())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setMarginBottom(20));

            document.add(new Paragraph("\n\n"));

            // ================================
            // 🟦 3. Pie de página Derecha
            // ================================
            Paragraph footer = new Paragraph("Generado por: Sistema Check-In")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final AsistenciaRepository asistenciaRepository;

    public PdfService(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    // 📊 Estadísticas globales de asistencia usando el enum
    public byte[] generarEstadisticasAsistencia() {
        try {
            List<Asistencia> asistencias = asistenciaRepository.findAll();

            long total = asistencias.size();
            long presentes = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE).count();
            long ausentes = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE).count();
            long tarde = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.TARDE).count();
            long justificados = asistencias.stream().filter(a -> a.getEstado() == EstadoAsistencia.JUSTIFICADO).count();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("📊 REPORTE GLOBAL DE ASISTENCIAS"));
            document.add(new Paragraph("Total registros: " + total));
            document.add(new Paragraph("Presentes: " + presentes));
            document.add(new Paragraph("Ausentes: " + ausentes));
            document.add(new Paragraph("Tarde: " + tarde));
            document.add(new Paragraph("Justificados: " + justificados));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}