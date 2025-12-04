package com.Check_In.Check__In1.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "carnet_digital")
public class CarnetDigital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String nombreCompleto;

    @Column(length = 500)
    private String numeroDeIdentificacion;

    @Column(nullable = true, length = 500)
    private String ficha;

    @Column(nullable = true, length = 500)
    private String programa;

    @Column(nullable = true, length = 500)
    private String jornada;

    @Column(nullable = false)
    private String foto;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNumeroDeIdentificacion() {
        return numeroDeIdentificacion;
    }

    public void setNumeroDeIdentificacion(String numeroDeIdentificacion) {
        this.numeroDeIdentificacion = numeroDeIdentificacion;
    }

    public String getFicha() {
        return ficha;
    }

    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto( String foto) {
        this.foto = foto;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
