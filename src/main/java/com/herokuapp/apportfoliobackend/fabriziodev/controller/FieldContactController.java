package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.FieldContactDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Experiencia;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.FieldContact;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.FieldContactService;
import io.swagger.annotations.ApiOperation;
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
public class FieldContactController {

    @Autowired
    FieldContactService fieldContactService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("fieldcontact")
    @ResponseBody
    public ResponseEntity<List<FieldContact>> listarTodos() {
        List<FieldContact> list = fieldContactService.listAll();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("fieldcontact/{id}")
    @ResponseBody
    public ResponseEntity<Experiencia> listfdbyid(@PathVariable Integer id) throws Exception {
        FieldContact fd = fieldContactService.listbyid(id);
        return new ResponseEntity(fd, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("fieldcontact")
    @ResponseBody
    public ResponseEntity<FieldContactDTO> nuevo(@Valid @RequestBody FieldContactDTO dto, Authentication authentication) throws Exception {
        /*if (dto.getValueName() == null) {
            return new ResponseEntity(new Mensaje("El value name es obligatorio"), HttpStatus.BAD_REQUEST);
        }*/

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        FieldContact fd = new FieldContact();

        fd.setValueName("contacto");
        fd.setTelValue(dto.isTelValue());
        fd.setSubjectValue(dto.isSubjectValue());
        fd.setUsuarioId(usuario.getId());
        fd.setCreatedAt(LocalDateTime.now());

        fieldContactService.guardar(fd);
//        return new ResponseEntity(new Mensaje("Field contact guardado con éxito"), HttpStatus.OK);
        return new ResponseEntity(fd, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("fieldcontact/{id}/editar")
    @ResponseBody
    public ResponseEntity<FieldContactDTO> edit(@PathVariable("id") int id, @Valid @RequestBody FieldContactDTO dto, Authentication authentication) throws Exception {
        if (dto.getValueName() == null) {
            return new ResponseEntity(new Mensaje("El value name es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (!fieldContactService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        FieldContact fd = new FieldContact();
        fd.setId(fieldContactService.listbyid(id).getId());
        fd.setValueName(fieldContactService.listbyid(id).getValueName());
        fd.setTelValue(dto.isTelValue());
        fd.setSubjectValue(dto.isSubjectValue());
        fd.setUsuarioId(usuario.getId());
        fd.setCreatedAt(fieldContactService.listbyid(id).getCreatedAt());
        fd.setEditedAt(LocalDateTime.now());

        fieldContactService.guardar(fd);
//        return new ResponseEntity(new Mensaje("Field contact editado con éxito"), HttpStatus.OK);
        return new ResponseEntity(fd, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("fieldcontact/{id}/eliminar")
    public ResponseEntity<Integer> eliminarExperiencia(@PathVariable("id") int id, Authentication authentication) throws Exception {
        if (!fieldContactService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        fieldContactService.borrar(id);
        return new ResponseEntity(new Mensaje("Field contact eliminado con éxito"), HttpStatus.OK);
    }

}
