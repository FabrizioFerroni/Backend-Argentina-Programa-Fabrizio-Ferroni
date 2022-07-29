package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contactotelsub;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.ContactotelsubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactotelsubService {
    @Autowired
    ContactotelsubRepository fdRepo;

    public List<Contactotelsub> listAll() {
        return fdRepo.findAllByOrderByIdDesc();
    }

    public Contactotelsub listbyid(Integer id) throws Exception {
        Contactotelsub fd = fdRepo.findById(id).orElseThrow(() -> new Exception("El campo de contacto no existe"));
        return fd;
    }

    public void guardar(Contactotelsub fd) {
        fdRepo.save(fd);
    }

    public void borrar(Integer id) {
        fdRepo.deleteById(id);
    }

    public boolean existsById(int id) {
        return fdRepo.existsById(id);
    }

}
