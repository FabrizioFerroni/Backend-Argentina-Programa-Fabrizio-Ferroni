package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Proyectos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectosRepository extends JpaRepository<Proyectos, Integer> {

    List<Proyectos> findAllByOrderByIdDesc();


    //    SELECT * FROM `proyectos` ORDER BY `edited_at` DESC
    @Query(nativeQuery = true, value = "SELECT * FROM proyectos ORDER BY edited_at DESC")
    List<Proyectos> findAllByOrderByEditedAtDesc();

}
