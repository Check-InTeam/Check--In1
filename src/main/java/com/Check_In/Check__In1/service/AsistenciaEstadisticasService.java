package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AsistenciaEstadisticasService {

    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaEstadisticasService(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    public Map<EstadoAsistencia, Long> obtenerEstadisticas() {
        List<Object[]> resultados = asistenciaRepository.contarPorEstado();

        Map<EstadoAsistencia, Long> estadisticas = new EnumMap<>(EstadoAsistencia.class);
        for (Object[] fila : resultados) {
            EstadoAsistencia estado = (EstadoAsistencia) fila[0];
            Long cantidad = (Long) fila[1];
            estadisticas.put(estado, cantidad);
        }

        return estadisticas;
    }
}

