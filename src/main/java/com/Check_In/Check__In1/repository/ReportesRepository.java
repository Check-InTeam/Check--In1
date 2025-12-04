package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.entity.TipoReporte;
import com.Check_In.Check__In1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportesRepository extends JpaRepository<Reportes, Long> {

    List<Reportes> findByUserId(Long userId);

    List<Reportes> findByUserNombre(String nombre);


    List<Reportes> findAllByUser(User user);

    boolean existsByUserIdAndTipo(Long userId, TipoReporte tipo);

    int countByUser_Nombre(String nombre);



}

