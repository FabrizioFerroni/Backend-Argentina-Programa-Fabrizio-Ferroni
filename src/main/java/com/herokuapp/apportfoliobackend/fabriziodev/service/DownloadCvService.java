package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.entity.DownloadCv;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.DownloadCvRepository;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DownloadCvService {

    @Autowired
    DownloadCvRepository downloadCvRepository;

    public List<DownloadCv> listarTodos() {
        return downloadCvRepository.findAllByOrderByIdDesc();
    }

    public DownloadCv getDownloadCvById(Integer id) throws Exception {
        DownloadCv downloadCv = downloadCvRepository.findById(id).orElseThrow(() -> new Exception("No se ha descargado este CV"));
        return downloadCv;
    }

   /* public Usuario getDownloadCvByUser(String username) throws Exception {
        Usuario user_dcv = downloadCvRepository.findByNombreUsuario(username).orElseThrow(()   -> new Exception("No se ha descargado este CV"));
        return user_dcv;
    }*/

    public List<DownloadCv> GetDownloadCvByUser(Integer id) throws Exception {
        return downloadCvRepository.findByIdForUser(id);
//        return downloadCv;
    }

    public void guardar(DownloadCv downloadCv) {
        downloadCvRepository.save(downloadCv);
    }

    public void borrar(Integer id) {
        downloadCvRepository.deleteById(id);
    }

    public boolean existsById(int id){
        return downloadCvRepository.existsById(id);
    }


}
