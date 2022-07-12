package com.herokuapp.apportfoliobackend.fabriziodev.service;


import com.herokuapp.apportfoliobackend.fabriziodev.entity.Experiencia;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.ExperienciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExperienciaService {

    @Autowired
    ExperienciaRepository experienciaRepository;

    public List<Experiencia> listarTodos() {
        return experienciaRepository.findAllByOrderByIdDesc();
    }

    public Experiencia getExperienciaById(Integer id) throws Exception {
        Experiencia experiencia = experienciaRepository.findById(id).orElseThrow(() -> new Exception("La experiencia no existe"));
        return experiencia;
    }

    public void guardar(Experiencia experiencia) {
        experienciaRepository.save(experiencia);
    }

    public void borrar(Integer id) {
        experienciaRepository.deleteById(id);
    }

    public boolean existsById(int id){
        return experienciaRepository.existsById(id);
    }

}
