package com.Check_In.Check__In1.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "justificacion")
public class Justificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Detalle escrito por el aprendiz
    @Column(nullable = false)
    private String motivo;

    // Fecha de la justificación
    @Column(nullable = false)
    private LocalDate fecha;

    // Ruta o nombre del archivo subido
    @Column(nullable = false)
    private String archivo;

    // Estado de la justificación (Ej: EnProceso, Aprobada, Rechazada)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoJustificacion estado;

    // Tipo de justificación (CITA_MEDICA, INCAPACIDAD, etc.)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50) check (tipo in ('Cita Medica','Incapacidad','Otro'))")
    private TipoJustificacion tipo;


    // Relación con el usuario que sube la justificación
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String comentario;

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public EstadoJustificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoJustificacion estado) {
        this.estado = estado;
    }

    public TipoJustificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoJustificacion tipo) {
        this.tipo = tipo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
