package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ChangePasswordDTO {
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String tokenPassword;

    private String nombre;

    private String mailTo;

    private String subject;

    public ChangePasswordDTO() {
    }

    public ChangePasswordDTO(String password, String confirmPassword, String tokenPassword, String nombre, String  mailTo, String subject) {
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.tokenPassword = tokenPassword;
        this.nombre = nombre;
        this.mailTo = mailTo;
        this.subject = subject;
    }
}
