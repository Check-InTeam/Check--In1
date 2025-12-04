package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.RegistroEquipos;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.RegistroEquiposRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegistroEquiposService {

    @Autowired
    private RegistroEquiposRepository registroEquiposRepository;

    public List<RegistroEquipos> getAllRegistroEquipos() {
        return registroEquiposRepository.findAll();
    }

    public Optional<RegistroEquipos> getRegistroEquiposById(int id) {
        return registroEquiposRepository.findById(id);
    }

    public RegistroEquipos saveRegistroEquipos(RegistroEquipos registroEquipos) {
        return registroEquiposRepository.save(registroEquipos);
    }

    public void deleteRegistroEquiposById(int id) {
        registroEquiposRepository.deleteById(id);
    }

    public List<RegistroEquipos> getRegistroEquiposByMarca(String marca) {
        return registroEquiposRepository.findByMarca(marca);
    }

    public List<RegistroEquipos> getRegistroEquiposBySerialEquipo(String serialEquipo) {
        return registroEquiposRepository.findBySerialEquipo(serialEquipo);
    }

    public List<RegistroEquipos> getRegistroEquiposByUserNombre(String nombre) {
        return registroEquiposRepository.findByUser_Nombre(nombre);
    }

    public List<RegistroEquipos> getRegistroEquiposByUser(User user) {
        return registroEquiposRepository.findByUser(user);
    }


    public List<RegistroEquipos> getEquiposByUser(User aprendiz) {
        return registroEquiposRepository.findByUser(aprendiz);
    }
}

