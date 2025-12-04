package com.Check_In.Check__In1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;


    @Column(unique = true, nullable = false)
    private String email;
    private String password;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Asistencia> asistencias;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Programacion> programacion;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CarnetDigital> carnets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RegistroEquipos> registros;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reportes> reportes;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Justificacion> justificacion;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

   public List<Asistencia> getAsistencias() {
        return asistencias;
   }

   public void setAsistencias(List<Asistencia> asistencias) {
        this.asistencias = asistencias;
   }

    public List<Programacion> getProgramacion() {
        return programacion;
    }

    public void setProgramacion(List<Programacion> programacion) {
        this.programacion = programacion;
    }

    public List<CarnetDigital> getCarnets() {
        return carnets;
    }

    public void setCarnets(List<CarnetDigital> carnets) {
        this.carnets = carnets;
    }

    public List<RegistroEquipos> getRegistros() {
        return registros;
    }

    public void setRegistros(List<RegistroEquipos> registros) {
        this.registros = registros;
    }

    public List<Reportes> getReportes() {
        return reportes;
    }

    public void setReportes(List<Reportes> reportes) {
        this.reportes = reportes;
    }
    public List<Justificacion> getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(List<Justificacion> justificacion) {
        this.justificacion = justificacion;
    }
}
