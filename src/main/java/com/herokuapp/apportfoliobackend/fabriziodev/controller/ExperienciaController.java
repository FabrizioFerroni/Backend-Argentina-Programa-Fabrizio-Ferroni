package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ExperienciaDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Experiencia;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.ExperienciaService;
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
public class ExperienciaController {

    @Autowired
    ExperienciaService experienciaService;

    @Autowired
    UsuarioService usuarioService;


    @GetMapping("experiencias")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros obtenidos en la BD")
    public ResponseEntity<List<Experiencia>> listarTodos() {
        List<Experiencia> list = experienciaService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("experiencia/{id}")
    @ApiOperation(value = "Lista el registro obtenido en la BD segun su id")
    @ResponseBody
    public ResponseEntity<Experiencia> getExperienciaById(@PathVariable Integer id) throws Exception {
        Experiencia experiencia = experienciaService.getExperienciaById(id);
        return new ResponseEntity(experiencia, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("experiencia")
    @ResponseBody
    @ApiOperation(value = "Inserta un nuevo registro a la BD")
    public ResponseEntity<ExperienciaDTO> newExperience(@Valid @RequestBody ExperienciaDTO expdto, BindingResult result, Authentication authentication) throws Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        if(expdto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El título es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(expdto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if(expdto.getPeriodo() == null) {
            return new ResponseEntity(new Mensaje("El periodo es obligatorio"), HttpStatus.BAD_REQUEST);
        }


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Experiencia experiencia = new Experiencia();

        experiencia.setTitulo(expdto.getTitulo());
        experiencia.setDescripcion(expdto.getDescripcion());
        experiencia.setPeriodo(expdto.getPeriodo());
        experiencia.setPeriodoDesde(expdto.getPeriodoDesde());
        experiencia.setPeriodoHasta(expdto.getPeriodoHasta());


        experiencia.setUsuario_id(usuario.getId());
        experiencia.setCreatedAt(LocalDateTime.now());


        experienciaService.guardar(experiencia);
        return new ResponseEntity(new Mensaje("Experiencia guardada con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("experiencia/{id}/editar")
    @ApiOperation(value = "Actualiza un registro en la BD segun el id obtenido")
    public ResponseEntity<ExperienciaDTO> editarExperiencia(@PathVariable("id") int id,
                                               @RequestBody ExperienciaDTO expdto,
                                               Authentication authentication) throws Exception {

       // Experiencia exp = experienciaService.getExperienciaById(id);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        if (!experienciaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }


        if(expdto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El título es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(expdto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if(expdto.getPeriodo() == null) {
            return new ResponseEntity(new Mensaje("El periodo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        Experiencia experiencia = new Experiencia();


        experiencia.setId(experienciaService.getExperienciaById(id).getId());
        experiencia.setTitulo(expdto.getTitulo());
        experiencia.setDescripcion(expdto.getDescripcion());
        experiencia.setPeriodo(expdto.getPeriodo());
        experiencia.setUsuario_id(usuario.getId());
        experiencia.setCreatedAt(experienciaService.getExperienciaById(id).getCreatedAt());
        experiencia.setEditedAt(LocalDateTime.now());
        experiencia.setPeriodoDesde(expdto.getPeriodoDesde());
        experiencia.setPeriodoHasta(expdto.getPeriodoHasta());
        experienciaService.guardar(experiencia);
        return new  ResponseEntity(new Mensaje("Experiencia guardada con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("experiencia/{id}/eliminar")
    @ApiOperation(value = "Borra un registro en la BD segun su id")
    public ResponseEntity<Integer> eliminarExperiencia(@PathVariable("id") int id, Authentication authentication) throws Exception {
        if (!experienciaService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

            experienciaService.borrar(id);
        return new ResponseEntity(new Mensaje("Experiencia eliminada con éxito"), HttpStatus.OK);
    }
}
