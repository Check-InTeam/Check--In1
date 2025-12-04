package com.Check_In.Check__In1.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    @Column(nullable = false)
    private int numeroFallas = 0;

    @Column(nullable = false)
    private boolean advertencia = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public void setTipo(TipoReporte tipo) {
        this.tipo = tipo;
    }

    public int getNumeroFallas() {
        return numeroFallas;
    }

    public void setNumeroFallas(int numeroFallas) {
        this.numeroFallas = numeroFallas;
    }

    public boolean isAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia(boolean advertencia) {
        this.advertencia = advertencia;
    }


}
