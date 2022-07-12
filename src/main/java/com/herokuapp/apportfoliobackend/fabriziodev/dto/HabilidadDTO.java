package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class HabilidadDTO {
    @NotNull
    @Column(name="titulo", nullable=false)
    private String titulo;

    @NotNull
    @Column(name="descripcion", nullable=false, length=2048)
    private String descripcion;
    @Column(nullable = false)
    @NotNull
    private String imagenName;
    @Column(nullable = false)
    @NotNull
    private String imagenUrl;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public HabilidadDTO() {
    }

    public HabilidadDTO(String titulo, String descripcion, String imagenName, String imagenUrl, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenName = imagenName;
        this.imagenUrl = imagenUrl;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
