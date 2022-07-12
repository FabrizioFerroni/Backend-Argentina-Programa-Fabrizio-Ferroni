package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BackendDTO {
    @Column(nullable = false)
    @NotNull
    private String nombre;

    @Column(nullable = false)
    @NotNull
    private Integer porcentaje;

    @Column(nullable = false)
    @NotNull
    private String classicon;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;


    public BackendDTO() {
    }

    public BackendDTO(String nombre, Integer porcentaje, String classicon, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.classicon = classicon;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
