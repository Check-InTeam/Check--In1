package com.Check_In.Check__In1.service;

import com.Check_In.Check__In1.entity.Role;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.RoleRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public List<User> getUserByNombre(String nombre) {
        return userRepository.findByNombre(nombre);
    }


    public User saveUser(User user) {
        String email = user.getEmail().toLowerCase();
        String roleName = null;

        if (email.endsWith("@admin.com")) {
            roleName ="ADMINISTRADOR";
        } else if (email.endsWith("@sena.edu.co")) {
            roleName = "INSTRUCTOR";
        } else if (email.endsWith("@gmail.com")||
                    email.endsWith("@hotmail.com") ||
                    email.endsWith("@yahoo.com.co")) {
            roleName = "APRENDIZ";
        }

        if (roleName == null) {
            throw new IllegalArgumentException("El correo no pertenece a un dominio permitido.");
        }


        Role role = roleRepository.findByNombre(roleName);

        if (role == null) {
            role = new Role();
            role.setNombre(roleName);
            roleRepository.save(role);
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

}


