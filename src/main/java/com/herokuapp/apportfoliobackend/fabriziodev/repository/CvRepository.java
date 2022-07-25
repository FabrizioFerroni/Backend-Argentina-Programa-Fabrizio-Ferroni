package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvRepository extends JpaRepository<Cv, Integer> {
    List<Cv> findAllByOrderByIdDesc();

    @Query(nativeQuery = true, value = "SELECT * FROM cv ORDER BY id DESC LIMIT 1")
    List<Cv> findLastRegister();

//    @Query(nativeQuery = true, value = "DELETE FROM download_cv WHERE cv_id=:id")
    @Query(nativeQuery = false, value = "DELETE FROM download_cv WHERE cv_id=:id")
    void deletedown_cv(Integer id);

}
