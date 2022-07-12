package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Header {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    private String apellido;

    @NotNull
    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    @NotNull
    private String imagenNAME;

    @Column(nullable = false)
    @NotNull
    private String imagenURL;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public Header() {
    }

    public Header(int id, String nombre, String apellido, String descripcion, String imagenNAME, String imagenURL,  Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
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
