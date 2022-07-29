package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.FieldContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldContactRepository extends JpaRepository<FieldContact, Integer> {
    List<FieldContact> findAllByOrderByIdDesc();
}
