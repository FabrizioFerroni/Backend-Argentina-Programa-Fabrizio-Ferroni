package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {
    List<Contacto> findAllByOrderByIdDesc();
}
