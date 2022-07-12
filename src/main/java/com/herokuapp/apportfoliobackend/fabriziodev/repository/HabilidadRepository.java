package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Habilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabilidadRepository extends JpaRepository<Habilidad, Integer> {


        List<Habilidad> findAllByOrderByIdDesc();

}
