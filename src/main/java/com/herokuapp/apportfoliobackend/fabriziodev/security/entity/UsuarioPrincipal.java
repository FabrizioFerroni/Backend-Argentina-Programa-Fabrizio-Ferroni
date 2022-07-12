package com.herokuapp.apportfoliobackend.fabriziodev.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioPrincipal implements UserDetails {

    private int id;
    private String nombre;
    private String apellido;
    private String nombreUsuario;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String avatar;

    public UsuarioPrincipal(int id, String nombre, String apellido, String nombreUsuario, String email, String password, Collection<? extends GrantedAuthority> authorities, String avatar) {

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.avatar = avatar;
    }

    public static UsuarioPrincipal build(Usuario usuario) {
        List<GrantedAuthority> authorities
                = usuario.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol
                .getRolNombre().name())).collect(Collectors.toList());
        return new UsuarioPrincipal(usuario.getId(), usuario.getNombre(), usuario.getApellido(), usuario.getNombreUsuario(), usuario.getEmail(), usuario.getPassword(), authorities, usuario.getAvatar());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public int getId(){
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

}