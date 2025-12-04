package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByNombre(String nombre);
    boolean existsByNombre(String nombre);

    Role findByNombreIgnoreCase(String nombre);
}

