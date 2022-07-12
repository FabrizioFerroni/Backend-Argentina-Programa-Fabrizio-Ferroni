package com.herokuapp.apportfoliobackend.fabriziodev.entity;


import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AcercaDeHome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotNull
    private String titulo;

    @NotNull
    @Column(name="descripcion", nullable=false, length=1024)
    private String descripcion;

    @NotNull
    @Column(name="descripcion2", nullable=false, length=1024)
    private String descripcion2;

    @Column(nullable = false)
    @NotNull
    private String link;

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

    public AcercaDeHome() {
    }

    public AcercaDeHome(int id, String titulo, String descripcion, String descripcion2, String link, String imagenNAME, String imagenURL, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.descripcion2 = descripcion2;
        this.link = link;
        this.imagenNAME = imagenNAME;
        this.imagenURL = imagenURL;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
