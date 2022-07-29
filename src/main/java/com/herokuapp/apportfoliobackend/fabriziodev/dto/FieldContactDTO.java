package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class FieldContactDTO {

    @Column(nullable = false)
    @NotNull
    private String ValueName;

    @Column(nullable = false)
    @NotNull
    private boolean DataValue;

    @Column(nullable = false)
    @NotNull
    private int UsuarioId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public FieldContactDTO() {
    }

    public FieldContactDTO(String valueName, boolean dataValue, int usuarioId, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.ValueName = valueName;
        this.DataValue = dataValue;
        this.UsuarioId = usuarioId;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
