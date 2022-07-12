package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Backend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackendRepository extends JpaRepository<Backend, Integer> {
    List<Backend> findAllByOrderByIdDesc();
}
