package com.Check_In.Check__In1.service;


import com.Check_In.Check__In1.entity.Programacion;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.factory.ProgramacionDesdeArchivoFactory;
import com.Check_In.Check__In1.repository.ProgramacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramacionService {

    @Autowired
    private ProgramacionRepository programacionRepository;

    @Autowired
    private ProgramacionDesdeArchivoFactory   programacionDesdeArchivoFactory;

    public List<Programacion> getAllProgramacion() {
        return programacionRepository.findAll();
    }

    public Optional<Programacion> getProgramacionById(int id) {
        return programacionRepository.findById(id);
    }

    public Programacion saveProgramacion(Programacion programacion) {
        return programacionRepository.save(programacion);
    }

    public List<Programacion> saveAll(List<Programacion> programaciones) {
        return programacionRepository.saveAll(programaciones);
    }
    public void deleteProgramacion(int id) {
        programacionRepository.deleteById(id);
    }

    public List<Programacion> getProgramacionByFicha(String ficha) {
        return programacionRepository.findByFicha(ficha);
    }

    public List<Programacion> getProgramacionByAmbiente(String ambiente) {
        return programacionRepository.findByAmbiente(ambiente);
    }

    public List<Programacion> getProgramacionByNombreAsignatura(String nombre_asignatura) {
        return programacionRepository.findByNombreAsignatura(nombre_asignatura);
    }

    /**
     * Método general que decide si cargar CSV o Excel.
     */
    public List<Programacion> cargarDesdeArchivo(MultipartFile file, User user) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("El archivo no tiene nombre.");
        }

        if (filename.endsWith(".csv")) {
            return programacionDesdeArchivoFactory.cargarDesdeCSV(file, user);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return programacionDesdeArchivoFactory.cargarDesdeExcel(file, user);
        } else {
            throw new IllegalArgumentException("Formato no soportado. Use CSV o Excel (.xlsx/.xls).");
        }
    }

}
