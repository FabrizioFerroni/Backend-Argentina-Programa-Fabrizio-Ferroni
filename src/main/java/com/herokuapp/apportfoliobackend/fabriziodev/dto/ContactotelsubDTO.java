package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContactotelsubDTO {

    @Column(nullable = false)
    @NotNull
    private String nameTelsub;

    @Column(nullable = false)
    @NotNull
    private boolean op1;

    @Column(nullable = false)
    @NotNull
    private boolean op2;

    @Column(nullable = false)
    @NotNull
    private int usuarioId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public ContactotelsubDTO() {
    }

    public ContactotelsubDTO(String nameTelsub, boolean op1, boolean op2, int usuarioId, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.nameTelsub = nameTelsub;
        this.op1 = op1;
        this.op2 = op2;
        this.usuarioId = usuarioId;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
