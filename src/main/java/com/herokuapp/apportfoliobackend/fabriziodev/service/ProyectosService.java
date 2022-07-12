package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Proyectos;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.ProyectosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProyectosService {

    @Autowired
    ProyectosRepository proyectosRepository;

    public List<Proyectos> listarTodos() {
        return proyectosRepository.findAllByOrderByEditedAtDesc();
    }

    public Proyectos getProyectobyid(Integer id) throws Exception {
        Proyectos proyecto = proyectosRepository.findById(id).orElseThrow(() -> new Exception("El proyecto no existe"));
        return proyecto;
    }

    public void guardar(Proyectos proyecto) {
        proyectosRepository.save(proyecto);
    }

    public void borrar(Integer id) {
        proyectosRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return proyectosRepository.existsById(id);
    }
}
