package com.Check_In.Check__In1.controller;


import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) Long currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.badRequest().body("Debe proporcionar el ID del usuario actual");
        }
        Optional<User> currentUser = userService.getUserById(currentUserId);

        if (currentUser.isPresent()) {
            if (currentUser.get().getRole() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("El usuario no tiene un rol asignado");
            }

            String role = currentUser.get().getRole().getNombre();

            if ("ADMINISTRADOR".equalsIgnoreCase(role)) {
                List<User> users = userService.getAllUsers();
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acceso denegado. Solo los administradores pueden ver");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            return ResponseEntity.ok(userOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        User updated = userService.saveUser(user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam Long currentUserId) {
        Optional<User> currentUser = userService.getUserById(currentUserId);

        if (currentUser.isPresent() && "ADMINISTRADOR".equalsIgnoreCase(currentUser.get().getRole().getNombre())) {
            if (!userService.getUserById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado. Solo los administradores elimina usuarios");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getUserByNombre(@RequestParam String nombre, @RequestParam Long currentUserId) {
        try {
            Optional<User> currentUser = userService.getUserById(currentUserId);

            if (currentUser.isPresent() && "ADMINISTRADOR".equalsIgnoreCase(currentUser.get().getRole().getNombre())) {
                List<User> usuarios = userService.getUserByNombre(nombre);

                if (usuarios.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron usuarios con ese nombre");
                }

                return ResponseEntity.ok(usuarios);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acceso denegado. Solo los administradores pueden buscar usuarios");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }


}
