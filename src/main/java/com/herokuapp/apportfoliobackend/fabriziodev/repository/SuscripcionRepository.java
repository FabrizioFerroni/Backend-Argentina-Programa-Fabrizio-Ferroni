package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Integer> {

    Optional<Suscripcion> findByTokenSus(String tokenSub);

    Suscripcion findByEmail(String email);

    List<Suscripcion> findAllByOrderByIdDesc();

} 
