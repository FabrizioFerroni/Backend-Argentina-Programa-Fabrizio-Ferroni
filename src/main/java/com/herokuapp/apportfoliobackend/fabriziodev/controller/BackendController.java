package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.BackendDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Backend;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.BackendService;
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
public class BackendController {

    @Autowired
    BackendService backendService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("backend")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros que hay en la BD")
    public ResponseEntity<List<Backend>> getBackend() {
        List<Backend> list = backendService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("backend/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista el registro obtenido en la BD segun su id")
    public ResponseEntity<Backend> getBackendById(@PathVariable Integer id) throws Exception {
        Backend backend = backendService.getBackendById(id);
        return new ResponseEntity(backend, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("backend")
    @ApiOperation(value = "Inserta un nuevo registro a la BD")
    public ResponseEntity<BackendDTO> nuevo(@Valid @RequestBody BackendDTO backDto, BindingResult result, Authentication authentication) throws Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getPorcentaje() == null) {
            return new ResponseEntity(new Mensaje("El porcentaje es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getClassicon() == null) {
            return new ResponseEntity(new Mensaje("El icono es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Backend backend = new Backend();
        backend.setNombre(backDto.getNombre());
        backend.setPorcentaje(backDto.getPorcentaje());
        backend.setClassicon(backDto.getClassicon());
        backend.setCreatedAt(LocalDateTime.now());
        backend.setUsuario_id(usuario.getId());

        backendService.guardar(backend);

        return new ResponseEntity(new Mensaje("Backend guardado con éxito"), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("backend/{id}/editar")
    @ApiOperation(value = "Actualiza un registro en la bd segun su id")
    public ResponseEntity<BackendDTO> editar(@PathVariable("id") int id, @Valid @RequestBody BackendDTO backDto, BindingResult result, Authentication authentication) throws Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getPorcentaje() == null) {
            return new ResponseEntity(new Mensaje("El porcentaje es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if(backDto.getClassicon() == null) {
            return new ResponseEntity(new Mensaje("El icono es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (!backendService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Backend backend = new Backend();
        System.out.println("Backend creado el: " + backendService.getBackendById(id).getCreatedAt());
        backend.setId(backendService.getBackendById(id).getId());
        backend.setNombre(backDto.getNombre());
        backend.setPorcentaje(backDto.getPorcentaje());
        backend.setClassicon(backDto.getClassicon());
        backend.setCreatedAt(backendService.getBackendById(id).getCreatedAt());
        backend.setEditedAt(LocalDateTime.now());
        backend.setUsuario_id(usuario.getId());

        backendService.guardar(backend);

        return new ResponseEntity(new Mensaje("Backend editado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("backend/{id}/eliminar")
    @ApiOperation(value = "Borra un registro de la BD segun su id")
    public ResponseEntity<Integer> deleteFrontend(@PathVariable("id") int id) throws Exception {
        if (!backendService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        backendService.borrar(id);

        return new ResponseEntity(new Mensaje("Backend eliminado con éxito"), HttpStatus.OK);
    }
}
