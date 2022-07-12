package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Experiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotNull
    private String titulo;

    @Column(nullable = false)
    @NotNull
    private String periodo;

    @NotNull
    @Column(name="descripcion", nullable=false, length=1024)
    private String descripcion;


    @NotNull
    @Column(name="periodo_desde", nullable=false, length=255)
    private String periodoDesde;

    @NotNull
    @Column(name="periodo_hasta", nullable=false, length=255)
    private String periodoHasta;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public Experiencia() {
    }

    public Experiencia(int id, String titulo, String periodo, String descripcion, String periodoDesde, String periodoHasta, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.titulo = titulo;
        this.periodo = periodo;
        this.descripcion = descripcion;
        this.periodoDesde = periodoDesde;
        this.periodoHasta = periodoHasta;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
