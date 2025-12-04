package com.Check_In.Check__In1.service;


import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.entity.TipoReporte;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.ReportesRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReportesRepository reportesRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private UserRepository userRepository;



    public Reportes saveReporte(Reportes reporte) {
        return reportesRepository.save(reporte);
    }

    public Optional<Reportes> getReporteById(Long id) {
        return reportesRepository.findById(id);
    }

    public List<Reportes> getAllReportes() {
        return reportesRepository.findAll();
    }

    public void deleteReporteById(Long id) {
        reportesRepository.deleteById(id);
    }

    public List<Reportes> getReportesByUserId(Long userId) {
        return reportesRepository.findByUserId(userId);
    }

    public List<Reportes> getReportesByUserNombre(String nombre) {
        return reportesRepository.findByUserNombre(nombre);
    }


    public void registrarFalla(User user) {
        List<Reportes> optReporte = reportesRepository.findAllByUser(user);

        Reportes reporte;

        if (!optReporte.isEmpty()) {
            // Si ya existe, incrementa las fallas
            reporte = optReporte.get(0);
            reporte.setNumeroFallas(reporte.getNumeroFallas() + 1);

            // Si pasa de 3, activamos advertencia
            if (reporte.getNumeroFallas() > 3) {
                reporte.setAdvertencia(true);
            }

        } else {
            // Si no existe, creamos uno nuevo
            reporte = new Reportes();
            reporte.setUser(user);
            reporte.setTitulo("Reporte de asistencias");
            reporte.setDescripcion("Generado automáticamente por inasistencias");
            reporte.setTipo(TipoReporte.Academico); // ⚠️ necesitas tu enum
            reporte.setNumeroFallas(1);
            reporte.setAdvertencia(false);
        }

        reportesRepository.save(reporte);
    }

    public void generarReporteSiSuperaFaltas(Long userId) {
        User aprendiz = userRepository.findById(userId).orElseThrow();

        // Contar ausencias del aprendiz
        long ausencias = asistenciaRepository.countByUserIdAndEstado(userId, EstadoAsistencia.AUSENTE);

        // Verificar si supera 3 ausencias
        if (ausencias >= 3) {

            // Verificar si ya existe un reporte por ausencias repetidas
            boolean existe = reportesRepository.existsByUserIdAndTipo(userId, TipoReporte.Academico);
            if (!existe) {
                Reportes reporte = new Reportes();
                reporte.setUser(aprendiz);
                reporte.setTipo(TipoReporte.Academico);
                reporte.setTitulo("Reporte de Ausencias Reiteradas");
                reporte.setDescripcion("El aprendiz ha acumulado " + ausencias + " ausencias.");
                reportesRepository.save(reporte);
            }
        }
    }


}