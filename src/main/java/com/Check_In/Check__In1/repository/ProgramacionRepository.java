package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.Programacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramacionRepository extends JpaRepository<Programacion, Integer> {


    List<Programacion> findByNombreAsignatura(String nombreAsignatura);
    List<Programacion> findByFicha(String ficha);
    List<Programacion> findByAmbiente(String ambiente);


}
