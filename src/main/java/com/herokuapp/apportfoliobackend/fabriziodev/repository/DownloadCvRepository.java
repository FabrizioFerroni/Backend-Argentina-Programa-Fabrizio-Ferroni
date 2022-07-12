package com.herokuapp.apportfoliobackend.fabriziodev.repository;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.DownloadCv;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DownloadCvRepository extends JpaRepository<DownloadCv, Integer> {
    List<DownloadCv> findAllByOrderByIdDesc();

    //        Optional<Usuario> findByNombreUsuario(String nombreUsuario);
//    @Query(value = "SELECT * FROM download_cv WHERE usuario_id=:id;", nativeQuery=true)
    @Query(nativeQuery = true, value = "SELECT * FROM download_cv WHERE usuario_id=:id")
    List<DownloadCv> findByIdForUser(Integer id);
}
