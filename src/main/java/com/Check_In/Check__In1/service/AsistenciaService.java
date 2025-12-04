package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.Asistencia;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReporteService reporteService;

    public void registrarAsistencia(Long userId, EstadoAsistencia estado, String observaciones) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));

        Asistencia asistencia = new Asistencia();
        asistencia.setUser(user);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraEntrada(LocalTime.now());
        asistencia.setEstado(estado);
        asistencia.setObservaciones(observaciones);

        // Guardar asistencia
        asistenciaRepository.save(asistencia);

        // Generar reporte si corresponde
        if (estado == EstadoAsistencia.AUSENTE) {
            reporteService.generarReporteSiSuperaFaltas(userId);
        }
    }

    public List<Asistencia> getAsistenciasPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        return asistenciaRepository.findByUserAndEstado(user, EstadoAsistencia.AUSENTE);
    }

    public long contarAusencias(Long userId) {
        return asistenciaRepository.countByUserIdAndEstado(userId, EstadoAsistencia.AUSENTE);
    }

    public void generarAsistenciasDiarias() {
        LocalDate hoy = LocalDate.now();

        // Aquí obtenemos todos los aprendices
        List<User> aprendices = userRepository.findAll(); // filtrar solo aprendices si quieres

        for (User aprendiz : aprendices) {
            boolean yaRegistrada = asistenciaRepository.existsByUserAndFecha(aprendiz, hoy);
            if (!yaRegistrada) {
                Asistencia asistencia = new Asistencia();
                asistencia.setUser(aprendiz);
                asistencia.setFecha(hoy);
                asistencia.setHoraEntrada(LocalTime.now());

                // Por defecto AUSENTE
                asistencia.setEstado(EstadoAsistencia.AUSENTE);

                asistenciaRepository.save(asistencia);

                // Generar reporte si corresponde
                if (asistencia.getEstado() == EstadoAsistencia.AUSENTE) {
                    reporteService.generarReporteSiSuperaFaltas(aprendiz.getId());
                }
            }
        }
    }

}
