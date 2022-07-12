package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDeHome;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.AcercaDeHomeRepository;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AcercaDeHomeService {

    @Autowired
    AcercaDeHomeRepository acercaDeHomeRepository;

    public List<AcercaDeHome> listarTodos() {
        return acercaDeHomeRepository.findAllByOrderByIdDesc();
    }

    public AcercaDeHome getAcercaDeHomeById(Integer id) throws Exception {
        AcercaDeHome acercaDeHome = acercaDeHomeRepository.findById(id).orElseThrow(() -> new Exception("Este AcercaDeHome no existe"));
        return acercaDeHome;
    }

    public List<AcercaDeHome> getLastAcdh() throws Exception {
        return acercaDeHomeRepository.findLastRegister();
    }

    public void guardar(AcercaDeHome acercaDeHome) {
        acercaDeHomeRepository.save(acercaDeHome);
    }

    public void borrar(Integer id) {
        acercaDeHomeRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return acercaDeHomeRepository.existsById(id);
    }

    public AcercaDeHome existsByTitulo(String imagenNAME) {
        return acercaDeHomeRepository.findByTitulo(imagenNAME);
    }


}
