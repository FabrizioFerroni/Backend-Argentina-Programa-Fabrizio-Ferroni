package com.herokuapp.apportfoliobackend.fabriziodev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FieldContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotNull
    private String ValueName;

    @Column(nullable = false)
    @NotNull
    private boolean TelValue;

    @Column(nullable = false)
    @NotNull
    private boolean SubjectValue;

    @Column(nullable = false)
    @NotNull
    private int UsuarioId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public FieldContact() {
    }

    public FieldContact(int id, String valueName, boolean telValue, boolean subjectValue, int usuarioId, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.ValueName = valueName;
        this.TelValue = telValue;
        this.SubjectValue = subjectValue;
        this.UsuarioId = usuarioId;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}
