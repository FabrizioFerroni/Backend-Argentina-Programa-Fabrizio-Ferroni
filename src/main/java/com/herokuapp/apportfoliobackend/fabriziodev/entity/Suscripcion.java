package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Suscripcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(nullable = false)
    private String nombre;
    @NotNull
    @Column(nullable = false)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    @Column(nullable = false)
    @NotNull
    private String suscrito;

    @Column(nullable = false)
    @NotNull
    private String tokenSus;

    public Suscripcion() {
    }

    public Suscripcion(int id, String nombre, String email, LocalDateTime createdAt, LocalDateTime editedAt, String suscrito, String tokenSus) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
        this.suscrito = suscrito;
        this.tokenSus = tokenSus;
    }
    
    
}
