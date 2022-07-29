package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.FieldContact;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.FieldContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FieldContactService {
    @Autowired
    FieldContactRepository fdRepo;

    public List<FieldContact> listAll() {
        return fdRepo.findAllByOrderByIdDesc();
    }

    public FieldContact listbyid(Integer id) throws Exception {
        FieldContact fd = fdRepo.findById(id).orElseThrow(() -> new Exception("El campo de contacto no existe"));
        return fd;
    }

    public void guardar(FieldContact fd) {
        fdRepo.save(fd);
    }

    public void borrar(Integer id) {
        fdRepo.deleteById(id);
    }

    public boolean existsById(int id) {
        return fdRepo.existsById(id);
    }

}
