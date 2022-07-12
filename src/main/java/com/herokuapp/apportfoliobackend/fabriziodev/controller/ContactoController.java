package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ContactoDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contacto;
import com.herokuapp.apportfoliobackend.fabriziodev.service.ContactoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ContactoController {
    @Autowired
    ContactoService contactoService;

    @Value("${spring.mail.username}")
    private String mailFrom;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("contactos")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<List<Contacto>> listarTodos() {
        List lista = contactoService.listarContactos();
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("contacto/{id}")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<Contacto> listarPorId(@PathVariable("id") Integer id) throws Exception {
        Contacto contacto = contactoService.listarPorId(id);
        if (!contactoService.existsById(id)) {
            return new ResponseEntity(new Mensaje("No existe ningún contacto con ese id"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(contacto);
    }


    @PostMapping("contactame")
    @ApiOperation(value = "Metodo para contactarse conmigo, guarda un registro en la bd mientras que me envia un correo electronico")
    public ResponseEntity<ContactoDTO> sendEmailTemplateContacto(@Valid @RequestBody ContactoDTO dto) throws MessagingException {
        Contacto contacto = new Contacto();
        contacto.setNombre(dto.getNombre());
        dto.setNombre(dto.getNombre());
        contacto.setApellido(dto.getApellido());
        dto.setApellido(dto.getApellido());
        contacto.setEmail(dto.getEmail());
        dto.setEmail(dto.getEmail());
        contacto.setMensaje(dto.getMensaje());
        dto.setMensaje(dto.getMensaje());
        contacto.setCreatedAt(LocalDateTime.now());
        if (dto.getTelefono() != null || dto.getTelefono() != "") {
            contacto.setTelefono(dto.getTelefono());
            dto.setTelefono(dto.getTelefono());
        } else{
            contacto.setTelefono(null);
            dto.setTelefono(null);
        }
        if (dto.getSubject() != null) {
            contacto.setSubject(dto.getSubject());
            dto.setSubject(dto.getSubject());
        }else {
            contacto.setSubject("Te han contactado desde tu página web");
            dto.setSubject("Te han contactado desde tu página web");
        }
        contacto.setMailCc(dto.getEmail());
        dto.setMailCc(dto.getEmail());
        dto.setMailFrom(dto.getEmail());
        contacto.setMailFrom(dto.getEmail());


        contactoService.sendEmailContacto(dto);;
        contactoService.save(contacto);

        return new ResponseEntity(new Mensaje("Gracias por contactarte conmigo, pronto te responderemos tu mensaje"), HttpStatus.OK);
    }
}
