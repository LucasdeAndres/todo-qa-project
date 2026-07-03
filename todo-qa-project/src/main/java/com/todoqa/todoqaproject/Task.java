package com.todoqa.todoqaproject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 100, message = "El titulo no puede superar los 100 caracteres")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Size(max = 700, message = "La descripcion no puede superar los 700 caracteres")
    @Column(name = "descripcion")
    private String descripcion;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "TODO|IN_PROGRESS|DONE", message = "El estado debe ser TODO, IN_PROGRESS o DONE")
    @Column(name = "estado", nullable = false)
    private String estado;

    @NotBlank(message = "La prioridad es obligatoria")
    @Pattern(regexp = "BAJA|MEDIA|ALTA", message = "La prioridad debe ser BAJA, MEDIA o ALTA")
    @Column(name = "prioridad", nullable = false)
    private String prioridad = "MEDIA";


    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Task() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}