package com.Check_In.Check__In1.factory;

import com.Check_In.Check__In1.entity.Programacion;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.patrones.AuditoriaSingleton;
import com.Check_In.Check__In1.repository.ProgramacionRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ProgramacionDesdeArchivoFactory {

    @Autowired
    private ProgramacionRepository programacionRepository;

    @Autowired
    private UserRepository  userRepository;


    /**
     * Cargar programaciones desde un archivo CSV
     */
    public List<Programacion> cargarDesdeCSV(MultipartFile file, User usuarioAsignado) {
        List<Programacion> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) {
                    primera = false; // saltar encabezado
                    continue;
                }
                String[] datos = linea.split("[;,]");

                Programacion prog = new Programacion();
                prog.setNombreAsignatura(datos[0].trim());
                prog.setDescripcion(datos[1].trim());
                prog.setFicha(datos[2].trim());
                prog.setFechaInicio(LocalDate.parse(datos[3].trim())); // yyyy-MM-dd
                prog.setFechaFin(LocalDate.parse(datos[4].trim()));
                prog.setHoraInicio(LocalTime.parse(datos[5].trim()));  // HH:mm:ss
                prog.setHoraFin(LocalTime.parse(datos[6].trim()));
                prog.setAmbiente(datos[7].trim());
                prog.setUser(usuarioAsignado);

                lista.add(prog);

                AuditoriaSingleton.getInstance()
                        .registrar("Programación cargada (CSV): " + prog.getNombreAsignatura());
            }

            programacionRepository.saveAll(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Cargar programaciones desde un archivo Excel
     */
    public List<Programacion> cargarDesdeExcel(MultipartFile file, User usuarioAsignado) {
        List<Programacion> lista = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a", new Locale("es", "CO"));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // saltar cabecera

                Programacion prog = new Programacion();

                prog.setNombreAsignatura(row.getCell(0).getStringCellValue().trim());
                prog.setDescripcion(row.getCell(1).getStringCellValue().trim());

                Cell fichaCell = row.getCell(2);
                if (fichaCell.getCellType() == CellType.NUMERIC) {
                    prog.setFicha(String.valueOf((long) fichaCell.getNumericCellValue()));
                } else {
                    prog.setFicha(fichaCell.getStringCellValue().trim());
                }

                Cell fechaInicioCell = row.getCell(3);
                if (fechaInicioCell.getCellType() == CellType.NUMERIC) {
                    prog.setFechaInicio(fechaInicioCell.getLocalDateTimeCellValue().toLocalDate());
                } else {
                    String fechaStr = fechaInicioCell.getStringCellValue().trim();
                    prog.setFechaInicio(LocalDate.parse(fechaStr, dateFormatter));
                }


                Cell fechaFinCell = row.getCell(4);
                if (fechaFinCell.getCellType() == CellType.NUMERIC) {
                    prog.setFechaFin(fechaFinCell.getLocalDateTimeCellValue().toLocalDate());
                } else {
                    String fechaStr = fechaFinCell.getStringCellValue().trim();
                    prog.setFechaFin(LocalDate.parse(fechaStr, dateFormatter));
                }



                Cell horaInicioCell = row.getCell(5);
                if (horaInicioCell.getCellType() == CellType.NUMERIC) {
                    prog.setHoraInicio(horaInicioCell.getLocalDateTimeCellValue().toLocalTime());
                } else {
                    String horaStr = horaInicioCell.getStringCellValue().trim()
                            .replace(" ", " ")   // eliminar caracteres invisibles
                            .replace("a. m.", "AM")
                            .replace("p. m.", "PM");
                    prog.setHoraInicio(LocalTime.parse(horaStr, timeFormatter));
                }

                Cell horaFinCell = row.getCell(6);
                if (horaFinCell.getCellType() == CellType.NUMERIC) {
                    prog.setHoraFin(horaFinCell.getLocalDateTimeCellValue().toLocalTime());
                } else {
                    String horaStr = horaFinCell.getStringCellValue().trim()
                            .replace(" ", " ")
                            .replace("a. m.", "AM")
                            .replace("p. m.", "PM");
                    prog.setHoraFin(LocalTime.parse(horaStr, timeFormatter));
                }

                prog.setAmbiente(row.getCell(7).getStringCellValue().trim());

                prog.setUser(usuarioAsignado);

                lista.add(prog);

                AuditoriaSingleton.getInstance()
                        .registrar("Programación cargada (Excel): " + prog.getNombreAsignatura());
            }

            programacionRepository.saveAll(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
