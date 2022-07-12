package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Habilidad;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.HabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HabilidadService {

    @Autowired
    HabilidadRepository habilidadRepository;

    public List<Habilidad> findAll() {
        return habilidadRepository.findAllByOrderByIdDesc();
    }

    public Habilidad getHabilidadById(Integer id) throws Exception {
        Habilidad habilidad = habilidadRepository.findById(id).orElseThrow(() -> new Exception("Esta Habilidad no existe"));
        return habilidad;
    }

    public void guardar(Habilidad habilidad) {
        habilidadRepository.save(habilidad);
    }

    public void borrar(int id) {
        habilidadRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return habilidadRepository.existsById(id);
    }



}
