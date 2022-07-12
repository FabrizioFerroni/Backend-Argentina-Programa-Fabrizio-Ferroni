package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
public class ProyectosDTO {
    @NotNull
    @Column(name="titulo", nullable=false)
    private String titulo;

    @NotNull
    @Column(name="descripcion", nullable=false, length=2048)
    private String descripcion;

    @NotNull
    @Column(name="subtitulo", nullable=false)
    private String subtitulo;
    @Column(nullable = false)
    @NotNull
    private String imagenName;
    @Column(nullable = false)
    @NotNull
    private String imagenUrl;

    @Column(nullable = false)
    @NotNull
    private String creado;

    @Column(nullable = false)
    @NotNull
    private String link_demo;

    @Column(nullable = false)
    @NotNull
    private String link_github;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public ProyectosDTO() {
    }

    public ProyectosDTO(String titulo, String descripcion, String subtitulo, String imagenName, String imagenUrl, String creado, String link_demo, String link_github, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
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
