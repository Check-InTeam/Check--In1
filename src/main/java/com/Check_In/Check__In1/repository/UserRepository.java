package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.Role;
import com.Check_In.Check__In1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByNombre(String nombre);

    long countByRole_NombreIgnoreCase(String nombre);

    List<User> findByRole(Role role);

    List<User> findByRoleAndNombreContainingIgnoreCase(Role role, String nombre);

    List<User> findByRoleNombre(String nombreRol);

}
