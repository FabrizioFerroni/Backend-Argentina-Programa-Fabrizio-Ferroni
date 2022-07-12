package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Frontend;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.FrontendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class FrontendService {

    @Autowired
    FrontendRepository frontendRepository;

    public List<Frontend> listarTodos() {
        return frontendRepository.findAllByOrderByIdDesc();
    }

    public Frontend getFrontendById(Integer id) throws Exception {
        Frontend Frontend = frontendRepository.findById(id).orElseThrow(() -> new Exception("Este Frontend no existe"));
        return Frontend;
    }

    public void guardar(Frontend frontend) {
        frontendRepository.save(frontend);
    }

    public void borrar(Integer id) {
        frontendRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return frontendRepository.existsById(id);
    }
}
