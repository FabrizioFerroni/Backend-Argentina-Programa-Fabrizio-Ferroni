package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ContactotelsubDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Experiencia;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contactotelsub;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.ContactotelsubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/")
@ApiIgnore
public class ContactotelsubController {

    @Autowired
    ContactotelsubService ContactotelsubService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("fieldcontact")
    @ResponseBody
    public ResponseEntity<List<Contactotelsub>> listarTodos() {
        List<Contactotelsub> list = ContactotelsubService.listAll();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("fieldcontact/{id}")
    @ResponseBody
    public ResponseEntity<Experiencia> listfdbyid(@PathVariable Integer id) throws Exception {
        Contactotelsub fd = ContactotelsubService.listbyid(id);
        return new ResponseEntity(fd, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("fieldcontact")
    @ResponseBody
    public ResponseEntity<ContactotelsubDTO> nuevo(@Valid @RequestBody ContactotelsubDTO dto, Authentication authentication) throws Exception {
        /*if (dto.getValueName() == null) {
            return new ResponseEntity(new Mensaje("El value name es obligatorio"), HttpStatus.BAD_REQUEST);
        }*/

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Contactotelsub fd = new Contactotelsub();

        fd.setNameTelsub(dto.getNameTelsub());
        fd.setOp1(dto.isOp1());
        fd.setOp2(dto.isOp2());
        fd.setUsuarioId(usuario.getId());
        fd.setCreatedAt(LocalDateTime.now());

        ContactotelsubService.guardar(fd);
        return new ResponseEntity(new Mensaje("Field contact guardado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("fieldcontact/{id}/editar")
    @ResponseBody
    public ResponseEntity<ContactotelsubDTO> edit(@PathVariable("id") int id, @Valid @RequestBody ContactotelsubDTO dto, Authentication authentication) throws Exception {
        if (dto.getNameTelsub() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (!ContactotelsubService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Contactotelsub fd = new Contactotelsub();
        fd.setId(ContactotelsubService.listbyid(id).getId());
        fd.setNameTelsub(dto.getNameTelsub());
        fd.setOp1(dto.isOp1());
        fd.setOp2(dto.isOp2());
        fd.setUsuarioId(usuario.getId());
        fd.setCreatedAt(ContactotelsubService.listbyid(id).getCreatedAt());
        fd.setEditedAt(LocalDateTime.now());

        ContactotelsubService.guardar(fd);
        return new ResponseEntity(new Mensaje("Field contact editado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("fieldcontact/{id}/eliminar")
    public ResponseEntity<Integer> eliminarExperiencia(@PathVariable("id") int id, Authentication authentication) throws Exception {
        if (!ContactotelsubService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        ContactotelsubService.borrar(id);
        return new ResponseEntity(new Mensaje("Field contact eliminado con éxito"), HttpStatus.OK);
    }

}
