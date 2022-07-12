package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class EmailValuesDTO {
    private String mailFrom;
    private String mailCc;
    private String mailTo;
    private String subject;
    private String userName;

    private String nombre;
    private String tokenPassword;

    private LocalDateTime caducidadToken;

    public EmailValuesDTO() {

    }

    public EmailValuesDTO(String mailFrom, String mailCc, String mailTo, String subject, String userName, String nombre, String tokenPassword, LocalDateTime caducidadToken) {
        this.mailFrom = mailFrom;
        this.mailCc = mailCc;
        this.mailTo = mailTo;
        this.subject = subject;
        this.userName = userName;
        this.nombre = nombre;
        this.tokenPassword = tokenPassword;
        this.caducidadToken = caducidadToken;
    }
}
