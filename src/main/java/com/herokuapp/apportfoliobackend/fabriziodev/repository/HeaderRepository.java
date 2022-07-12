package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeaderRepository extends JpaRepository<Header, Integer> {

    List<Header> findAllByOrderByIdDesc();


    @Query(nativeQuery = true, value = "SELECT * FROM header ORDER BY id DESC LIMIT 1")
    List<Header> findLastRegister();

}
