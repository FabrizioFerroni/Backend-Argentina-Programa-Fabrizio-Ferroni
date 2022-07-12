package com.herokuapp.apportfoliobackend.fabriziodev.security.entity;

import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Pattern;

@Entity
@ApiIgnore
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Column(nullable = false)
    private String nombre;
    @NotNull
    @Column(nullable = false)
    private String apellido;
    @NotNull
    @Column(unique = true, nullable = false)
    private String nombreUsuario;
    @NotNull
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(nullable = false, unique = true)
    private String email;
    @NotNull
    @Column(nullable = false)
    private String password;

    private String tokenPassword;

    private LocalDateTime caducidadToken;
    @NotNull
    @Column(nullable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    private String avatar;

    private String imagenName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public Usuario() {
    }

    public Usuario( String nombre, String apellido, String nombreUsuario, String email, String password,   String avatar, String imagenName, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.imagenName = imagenName;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }

    public LocalDateTime getCaducidadToken() {
        return caducidadToken;
    }

    public void setCaducidadToken(LocalDateTime caducidadToken) {
        this.caducidadToken = caducidadToken;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImagenName() {
        return imagenName;
    }

    public void setImagenName(String imagenName) {
        this.imagenName = imagenName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
}
