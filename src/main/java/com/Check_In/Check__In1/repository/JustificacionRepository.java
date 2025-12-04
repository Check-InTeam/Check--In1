package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.EstadoJustificacion;
import com.Check_In.Check__In1.entity.Justificacion;
import com.Check_In.Check__In1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JustificacionRepository extends JpaRepository<Justificacion, Integer> {

    List<Justificacion> findByFecha(LocalDate fecha);
    List<Justificacion> findByUser_Nombre(String nombre);

    List<Justificacion> findByEstado(EstadoJustificacion estado);

    List<Justificacion> findByUserId(Long id);
    List<Justificacion> findByUser(User user);
}

