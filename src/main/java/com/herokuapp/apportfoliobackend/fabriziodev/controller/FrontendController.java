package com.herokuapp.apportfoliobackend.fabriziodev.controller;


import com.herokuapp.apportfoliobackend.fabriziodev.dto.FrontendDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Frontend;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.FrontendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class FrontendController {

    @Autowired
    FrontendService frontendService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("frontend")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros obtenidos de la BD")
    public ResponseEntity<List<Frontend>> getFrontend() {
        List<Frontend> list = frontendService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("frontend/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista el registro de la BD segun su id")
    public ResponseEntity<Frontend> getFrontendById(@PathVariable Integer id) throws Exception {
        Frontend frontend = frontendService.getFrontendById(id);
        return new ResponseEntity(frontend, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("frontend")
    @ApiOperation(value = "Inserta un nuevo registro a la BD")
    public ResponseEntity<FrontendDTO> nuevo(@Valid @RequestBody FrontendDTO frontDto, BindingResult result, Authentication authentication) throws Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getPorcentaje() == null) {
            return new ResponseEntity(new Mensaje("El porcentaje es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getClassicon() == null) {
            return new ResponseEntity(new Mensaje("El icono es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Frontend frontend = new Frontend();
        frontend.setNombre(frontDto.getNombre());
        frontend.setPorcentaje(frontDto.getPorcentaje());
        frontend.setClassicon(frontDto.getClassicon());
        frontend.setCreatedAt(LocalDateTime.now());
        frontend.setUsuario_id(usuario.getId());

        frontendService.guardar(frontend);

        return new ResponseEntity(new Mensaje("Frontend guardado con éxito"), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("frontend/{id}/editar")
    @ApiOperation(value = "Actualiza un registro en la BD segun su id")
    public ResponseEntity<FrontendDTO> editar(@PathVariable("id") int id, @Valid @RequestBody FrontendDTO frontDto, BindingResult result, Authentication authentication) throws Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getPorcentaje() == null) {
            return new ResponseEntity(new Mensaje("El porcentaje es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(frontDto.getClassicon() == null) {
            return new ResponseEntity(new Mensaje("El icono es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (!frontendService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Frontend frontend = new Frontend();
        System.out.println("Frontend Creado el: " + frontendService.getFrontendById(id).getCreatedAt());
        frontend.setId(frontendService.getFrontendById(id).getId());
        frontend.setNombre(frontDto.getNombre());
        frontend.setPorcentaje(frontDto.getPorcentaje());
        frontend.setClassicon(frontDto.getClassicon());
        frontend.setCreatedAt(frontendService.getFrontendById(id).getCreatedAt());
        frontend.setEditedAt(LocalDateTime.now());
        frontend.setUsuario_id(usuario.getId());

        frontendService.guardar(frontend);

        return new ResponseEntity(new Mensaje("Frontend editado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("frontend/{id}/eliminar")
    @ApiOperation(value = "Borra un registro en la BD segun su id")
    public ResponseEntity<Integer> deleteFrontend(@PathVariable("id") int id) throws Exception {
        if (!frontendService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        frontendService.borrar(id);

        return new ResponseEntity(new Mensaje("Frontend eliminado con éxito"), HttpStatus.OK);
    }



}
