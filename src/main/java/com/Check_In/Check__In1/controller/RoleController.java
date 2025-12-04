package com.Check_In.Check__In1.controller;


import com.Check_In.Check__In1.entity.Role;
import com.Check_In.Check__In1.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        if (role.getNombre() == null || role.getNombre().isBlank()) {
            return  ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        Role existing =  roleRepository.findByNombre(role.getNombre().toUpperCase());
        if (existing != null) {
            return  ResponseEntity.badRequest().body("Este rol ya existe");
        }

        role.setNombre(role.getNombre().toUpperCase());
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getRolesByName(@RequestParam String nombre) {
        Role role = roleRepository.findByNombre(nombre.toUpperCase());
        if (role == null) {
            return ResponseEntity.status(404).body("El rol no existe");
        }
        return ResponseEntity.ok(role);
    }

}

