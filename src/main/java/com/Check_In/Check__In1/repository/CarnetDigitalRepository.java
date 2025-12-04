package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.CarnetDigital;
import com.Check_In.Check__In1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarnetDigitalRepository extends JpaRepository<CarnetDigital, Integer> {
    List<CarnetDigital> findByUserId(Long userId);

    List<CarnetDigital> findByNombreCompleto(String nombreCompleto);

    Optional<CarnetDigital> findByUser(User user);

    Optional<CarnetDigital> findByNumeroDeIdentificacion(String numero);

}
