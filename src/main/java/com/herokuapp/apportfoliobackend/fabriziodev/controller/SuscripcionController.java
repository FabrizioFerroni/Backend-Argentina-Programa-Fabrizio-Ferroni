package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.SuscripcionDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Suscripcion;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.service.SuscripcionService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/")
public class SuscripcionController {


    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Autowired
    SuscripcionService susService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("suscriptores")
    @ApiIgnore
    @ResponseBody
    public ResponseEntity<List<Suscripcion>> listarSuscriptores() {
        List<Suscripcion> list = susService.listarTodos();

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("suscriptor/{id}")
    @ApiIgnore
    @ResponseBody
    public ResponseEntity<Suscripcion> getSuscriptorbyid(@PathVariable("id") Integer id) throws Exception {
        Suscripcion suscripcion = susService.getSuscbyid(id);
        return new ResponseEntity(suscripcion, HttpStatus.OK);
    }

    @PostMapping("nuevo-suscriptor")
    @ApiOperation(value = "Inserta un nuevo registro en la DB")
    public ResponseEntity<SuscripcionDTO> nuevoSuscriptor(@Valid @RequestBody SuscripcionDTO susDto, BindingResult result) throws IOException, Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("No se ha llenado ningun campo"), HttpStatus.BAD_REQUEST);
        }

        if (!isValid(susDto.getEmail())) {
            return new ResponseEntity(new Mensaje("El email ingresado no es valido"), HttpStatus.BAD_REQUEST);
        }

        Suscripcion sus = new Suscripcion();

        sus.setNombre(susDto.getNombre());
        sus.setEmail(susDto.getEmail());
        String randomCode = RandomString.make(64);
        sus.setTokenSus(randomCode);
        sus.setCreatedAt(LocalDateTime.now());
        sus.setEditedAt(LocalDateTime.now());
        String dayhoy = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        sus.setSuscrito(dayhoy);


        susService.guardar(sus);
        susService.sendEmailSus(sus);
        return new ResponseEntity(new Mensaje("Se registro tu suscripción con éxito"), HttpStatus.CREATED);

    }


    @GetMapping("getidwithtoken/{tokensub}")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<Map<String, Object>> getidwithtoken(@PathVariable String tokensub) {
        HashMap<String, Object> map = new HashMap<>();
        Optional<Suscripcion> suscripcionOpt = susService.getByTokenSub(tokensub);

        if (!suscripcionOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("El token no existe o ya fue dada de baja la suscripción"), HttpStatus.NOT_FOUND);
        }

        Suscripcion sus = new Suscripcion();
        sus.setId(suscripcionOpt.get().getId());
        sus.setNombre(suscripcionOpt.get().getNombre());
        sus.setEmail(suscripcionOpt.get().getEmail());

        susService.sendEmailUnsus(sus);

        int id = suscripcionOpt.get().getId();

        if (!susService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id del token no existe"), HttpStatus.NOT_FOUND);
        }

        map.put("id", id);

        return new ResponseEntity(map, HttpStatus.OK);
    }

    @DeleteMapping("suscriptor/{id}/borrar")
    @ApiOperation(value = "Borra un registro en la BD segun su id")
    public ResponseEntity<Suscripcion> borrarSusc(@PathVariable("id") Integer id, Suscripcion suss) throws Exception {
        if (susService.existsById(id)) {
            susService.borrar(id);
            return new ResponseEntity(new Mensaje("Tu suscripción se ha borrado con éxito"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("El suscriptor no existe"), HttpStatus.BAD_REQUEST);
        }
    }
}
