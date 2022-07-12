package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Frontend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrontendRepository extends JpaRepository<Frontend, Integer> {

        List<Frontend> findAllByOrderByIdDesc();

}
// End of user code
