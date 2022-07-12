package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcercaDeRepository  extends JpaRepository<AcercaDe, Integer> {
    List<AcercaDe> findAllByOrderByIdDesc();

    @Query(nativeQuery = true, value = "SELECT * FROM acerca_de ORDER BY id DESC LIMIT 1")
    List<AcercaDe> findLastRegister();
}
