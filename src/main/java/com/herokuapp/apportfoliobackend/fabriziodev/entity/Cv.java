package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
//@NoArgsConstructor
public class Cv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotNull
    private String nombreCv;
    @Column(nullable = false)
    @NotNull
    private String descripcionCv;
    @Column(nullable = false)
    @NotNull
    private String cvNAME;
    @Column(nullable = false)
    @NotNull
    private String cvURL;

    @Column(nullable = false)
    @NotNull
    private String cvUrlMovil;

    @Column(nullable = false)
    @NotNull
    private String nombredown;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public Cv() {
    }

    public Cv(int id, String nombreCv, String descripcionCv, String cvNAME, String cvURL, String cvUrlMovil, String nombredown, Integer usuario_id, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.nombreCv = nombreCv;
        this.descripcionCv = descripcionCv;
        this.cvNAME = cvNAME;
        this.cvURL = cvURL;
        this.cvUrlMovil = cvUrlMovil;
        this.nombredown = nombredown;
        this.usuario_id = usuario_id;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

}
