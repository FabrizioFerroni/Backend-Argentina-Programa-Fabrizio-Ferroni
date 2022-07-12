package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExperienciaDTO {
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
}
