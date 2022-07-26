package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DownloadCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
    private String rutaMovil;

    @Column(nullable = false)
    @NotNull
    private String nombredown;

    @Column(nullable = false)
    @NotNull
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
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

    public DownloadCv() {
    }

    public DownloadCv(int id, String nombre, String apellido, String nombreCv, String FDescarga, String ruta, String rutaMovil, String nombredown, String email, Integer cv_id, Integer usuario_id, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCv = nombreCv;
        this.FDescarga = FDescarga;
        this.ruta = ruta;
        this.rutaMovil = rutaMovil;
        this.nombredown = nombredown;
        this.email = email;
        this.cv_id = cv_id;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
    }
}
