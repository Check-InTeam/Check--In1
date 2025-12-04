package com.Check_In.Check__In1.service;


import com.Check_In.Check__In1.entity.CarnetDigital;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.CarnetDigitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarnetDigitalService {

    @Autowired
    private CarnetDigitalRepository carnetDigitalRepository;

    public List<CarnetDigital> getAllCarnetDigital() {
        return carnetDigitalRepository.findAll();
    }

    public Optional<CarnetDigital> getCarnetDigitalById(int id) {
        return carnetDigitalRepository.findById(id);
    }

    public List<CarnetDigital> getCarnetDigitalByUserId(Long UserId) {
        return carnetDigitalRepository.findByUserId(UserId);
    }

    public List<CarnetDigital> getCarnetsByNombreCompleto(String nombreCompleto) {
        return carnetDigitalRepository.findByNombreCompleto(nombreCompleto);
    }

    public CarnetDigital saveCarnet(CarnetDigital carnetDigital) {
        return carnetDigitalRepository.save(carnetDigital);
    }

    public void deleteCarnet(int id) {
        carnetDigitalRepository.deleteById(id);
    }


    // Lectura de carnet por usuario dentro de transacción de solo lectura
    @Transactional(readOnly = true)
    public Optional<CarnetDigital> getCarnetByUser(User user) {
        return carnetDigitalRepository.findByUser(user);
    }

}

