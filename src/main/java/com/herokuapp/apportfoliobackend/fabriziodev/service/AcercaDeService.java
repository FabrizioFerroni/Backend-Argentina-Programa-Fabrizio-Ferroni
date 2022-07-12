package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDe;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDeHome;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.AcercaDeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AcercaDeService {

    @Autowired
    AcercaDeRepository acercaDeRepository;

    public List<AcercaDe> listarTodos() {
        return acercaDeRepository.findAllByOrderByIdDesc();
    }

    public AcercaDe getAcercaDeById(Integer id) throws Exception {
        AcercaDe acercaDe = acercaDeRepository.findById(id).orElseThrow(() -> new Exception("Este AcercaDe no existe"));
        return acercaDe;
    }

    public List<AcercaDe> getLastAcd() throws Exception {
        return acercaDeRepository.findLastRegister();
    }

    public void save(AcercaDe acercaDe) {
        acercaDeRepository.save(acercaDe);
    }

    public void delete(Integer id) {
        acercaDeRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return acercaDeRepository.existsById(id);
    }

}
