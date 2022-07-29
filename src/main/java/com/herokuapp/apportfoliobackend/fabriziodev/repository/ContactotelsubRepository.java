package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contactotelsub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactotelsubRepository extends JpaRepository<Contactotelsub, Integer> {
    List<Contactotelsub> findAllByOrderByIdDesc();
}
