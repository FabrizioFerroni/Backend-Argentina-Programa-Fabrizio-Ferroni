package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Cv;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.CvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

@Service
@Transactional
public class CvService {

    private final Path rootFolder = Paths.get("uploads/docs");
    @Autowired
    CvRepository cvRepository;

    public List<Cv> listarTodos() {
        return cvRepository.findAllByOrderByIdDesc();
    }

    public Cv listarPorId(Integer id) throws Exception {
        Cv cv = cvRepository.findById(id).orElseThrow(() -> new Exception("El cv no existe"));
        return cv;
    }

    public List<Cv> getLastCv() throws Exception {
        return cvRepository.findLastRegister();
    }

    public void guardar(Cv cv) {
        cvRepository.save(cv);
    }

    public void borrar(Integer id) {
        cvRepository.deleteById(id);
    }

    public void borrardown_cv(Integer id) {
        cvRepository.deletedown_cv(id);
    }



    public boolean existsById(int id){
        return cvRepository.existsById(id);
    }

    public boolean existsById_down(int id){
        int exitid = cvRepository.exitsindown_cv(id);
        boolean isTrue = (exitid != 0);
        return isTrue;
    }

    public Resource load(String name) throws Exception {
        Path file = rootFolder.resolve(name);
        Resource resource = new UrlResource(file.toUri());
        return resource;
    }
}
