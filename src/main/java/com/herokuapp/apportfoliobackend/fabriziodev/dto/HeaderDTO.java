package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class HeaderDTO {

    @Column(nullable = false)
    @NotNull
    private String nombre;

    @Column(nullable = false)
    @NotNull
    private String apellido;

    @Column(nullable = false)
    @NotNull
    private String descripcion;
    @Column(nullable = false)
    @NotNull
    private String imagenNAME;
    @Column(nullable = false)
    @NotNull
    private String imagenURL;
    @Column(nullable = false)
    @NotNull
    private int usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public HeaderDTO() {
    }

    public HeaderDTO(String nombre, String apellido, String descripcion, String imagenNAME, String imagenURL, int usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.descripcion = descripcion;
        this.imagenNAME = imagenNAME;
        this.imagenURL = imagenURL;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
