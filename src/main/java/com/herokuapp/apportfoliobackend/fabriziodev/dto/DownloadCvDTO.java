package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class DownloadCvDTO {
    @Column(nullable = false)
    @NotNull
    private String nombre;

    @Column(nullable = false)
    @NotNull
    private String apellido;

    @Column(nullable = false)
    @NotNull
    private String nombreCv;

    @Column(nullable = false)
    @NotNull
    private String FDescarga;

    @Column(nullable = false)
    @NotNull
    private String ruta;

    @Column(nullable = false)
    @NotNull
    private String nombredown;

    @Column(nullable = false)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    private Integer cv_id;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    public DownloadCvDTO() {
    }

    public DownloadCvDTO(String nombre, String apellido, String nombreCv, String FDescarga, String ruta, String nombredown, String email, Integer cv_id, Integer usuario_id, LocalDateTime createdAt) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCv = nombreCv;
        this.FDescarga = FDescarga;
        this.ruta = ruta;
        this.nombredown = nombredown;
        this.email = email;
        this.cv_id = cv_id;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
    }
}
