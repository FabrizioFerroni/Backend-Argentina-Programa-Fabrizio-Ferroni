package com.herokuapp.apportfoliobackend.fabriziodev.service;

//import com.amazonaws.util.StringUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.HeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;

@Service
@Transactional
public class HeaderService {

    @Autowired
    HeaderRepository headerRepository;

    public List<Header> listarTodos() {
        return headerRepository.findAllByOrderByIdDesc();
    }

    public Header getHeaderbyid(Integer id) throws Exception {
        Header header = headerRepository.findById(id).orElseThrow(() -> new Exception("El header no existe"));
        return header;
    }



    public List<Header> getLastHeader() throws Exception {
        return headerRepository.findLastRegister();
    }



    public void guardar(Header header) {
        headerRepository.save(header);
    }

    public void borrar(Integer id) {
        headerRepository.deleteById(id);
    }


    public boolean existsById(int id){
        return headerRepository.existsById(id);
    }

}
