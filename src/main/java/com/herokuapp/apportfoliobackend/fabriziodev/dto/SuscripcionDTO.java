package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
public class SuscripcionDTO {
    @Column(nullable = false)
    @NotNull
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

    public SuscripcionDTO() {
    }

    public SuscripcionDTO(String nombre, String email, LocalDateTime createdAt, LocalDateTime editedAt, String suscrito, String tokenSus) {
        this.nombre = nombre;
        this.email = email;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
        this.suscrito = suscrito;
        this.tokenSus = tokenSus;
    }
}
