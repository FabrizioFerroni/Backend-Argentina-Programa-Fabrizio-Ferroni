package com.herokuapp.apportfoliobackend.fabriziodev.security.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByNombreUsuarioOrEmail(String nombreUsuario, String email);

    Optional<Usuario> findByTokenPassword(String tokenPassword);
    Optional<Usuario> findByverifyPassword(String verifyPassword);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByEmail(String email);

//    boolean isActiveUser_verify(boolean activeUser);


}
