package com.herokuapp.apportfoliobackend.fabriziodev.service;


import com.herokuapp.apportfoliobackend.fabriziodev.entity.Backend;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Frontend;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.BackendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BackendService {
    @Autowired
    BackendRepository backendRepository;

    public List<Backend> listarTodos() {
        return backendRepository.findAllByOrderByIdDesc();
    }

    public Backend getBackendById(Integer id) throws Exception {
        Backend backend = backendRepository.findById(id).orElseThrow(() -> new Exception("Este Backend no existe"));
        return backend;
    }

    public void guardar(Backend backend) {
        backendRepository.save(backend);
    }

    public void borrar(Integer id) {
        backendRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return backendRepository.existsById(id);
    }
}
