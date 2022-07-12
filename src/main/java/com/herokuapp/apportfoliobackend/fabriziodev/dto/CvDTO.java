package com.herokuapp.apportfoliobackend.fabriziodev.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

//@Getter
//@Setter
public class CvDTO {

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
    private String nombredown;
    @Column(nullable = false)
    @NotNull
    private Integer usuario_id;

    public CvDTO() {
    }

    public CvDTO(String nombreCv, String descripcionCv, String cvNAME, String cvURL, String nombredown, Integer usuario_id) {
        this.nombreCv = nombreCv;
        this.descripcionCv = descripcionCv;
        this.cvNAME = cvNAME;
        this.cvURL = cvURL;
        this.nombredown = nombredown;
        this.usuario_id = usuario_id;
    }

    public String getNombreCv() {
        return nombreCv;
    }

    public void setNombreCv(String nombreCv) {
        this.nombreCv = nombreCv;
    }

    public String getDescripcionCv() {
        return descripcionCv;
    }

    public void setDescripcionCv(String descripcionCv) {
        this.descripcionCv = descripcionCv;
    }

    public String getCvNAME() {
        return cvNAME;
    }

    public void setCvNAME(String cvNAME) {
        this.cvNAME = cvNAME;
    }

    public String getCvURL() {
        return cvURL;
    }

    public void setCvURL(String cvURL) {
        this.cvURL = cvURL;
    }

    public Integer getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Integer usuario_id) {
        this.usuario_id = usuario_id;
    }
}
