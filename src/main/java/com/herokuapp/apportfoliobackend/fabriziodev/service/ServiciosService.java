package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Servicios;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.ServiciosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiciosService {

    @Autowired
    ServiciosRepository serviciosRepository;


    public List<Servicios> listarTodos() {
        return serviciosRepository.findAllByOrderByIdDesc();
    }

    public Servicios getServiciosById(Integer id) throws Exception {
        Servicios servicios = serviciosRepository.findById(id).orElseThrow(() -> new Exception("Esta Servicios no existe"));
        return servicios;
    }

    public void guardar(Servicios servicios) {
        serviciosRepository.save(servicios);
    }

    public void borrar(int id) {
        serviciosRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return serviciosRepository.existsById(id);
    }
}
