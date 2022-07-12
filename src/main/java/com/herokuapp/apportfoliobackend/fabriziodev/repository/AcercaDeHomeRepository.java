package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDeHome;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcercaDeHomeRepository extends JpaRepository<AcercaDeHome, Integer> {
    AcercaDeHome findByTitulo(String imagenNAME);

    List<AcercaDeHome> findAllByOrderByIdDesc();

    @Query(nativeQuery = true, value = "SELECT * FROM acerca_de_home ORDER BY id DESC LIMIT 1")
    List<AcercaDeHome> findLastRegister();
}
