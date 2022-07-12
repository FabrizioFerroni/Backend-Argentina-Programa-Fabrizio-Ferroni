package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
public class Proyectos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name="titulo", nullable=false)
    private String titulo;

    @NotNull
    @Column(name="descripcion", nullable=false, length=2048)
    private String descripcion;

    @NotNull
    @Column(name="subtitulo", nullable=false)
    private String subtitulo;

    @NotNull
    @Column(nullable = false)
    private String imagenName;

    @Column(nullable = false)
    @NotNull
    private String imagenUrl;

    @NotNull
    @Column(nullable = false)
    private String creado;

    @NotNull
    @Column(nullable = false)
    private String link_demo;

    @NotNull
    @Column(nullable = false)
    private String link_github;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime editedAt;

    public Proyectos() {
    }

    public Proyectos(int id, String titulo, String descripcion, String subtitulo, String imagenName, String imagenUrl, String creado, String link_demo, String link_github, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.subtitulo = subtitulo;
        this.imagenName = imagenName;
        this.imagenUrl = imagenUrl;
        this.creado = creado;
        this.link_demo = link_demo;
        this.link_github = link_github;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
