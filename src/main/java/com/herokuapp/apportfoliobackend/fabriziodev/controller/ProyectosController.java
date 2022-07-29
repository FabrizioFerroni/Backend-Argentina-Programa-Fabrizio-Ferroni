package com.herokuapp.apportfoliobackend.fabriziodev.controller;


import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.ProyectosDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Proyectos;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.ProyectosService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import java.util.Date;
import java.util.Locale;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/")
public class ProyectosController {

    @Value("${web.upload-path-images-proyectos}")
    private String controllerPath;

    @Value("${web.upload-path-uploads}")
    private String uploads;

    @Value("${host.dns}")
    private String host;

    @Autowired
    ProyectosService proyectosService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    S3Service s3Service;

    @GetMapping("proyectos")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros de la BD")
    public ResponseEntity<List<Proyectos>> listarTodos() {
        List lista = proyectosService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("proyecto/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista el registro de la BD segun su id")
    public ResponseEntity<Proyectos> getProyectobyid(@PathVariable("id") Integer id) throws Exception {
        Proyectos proyecto = proyectosService.getProyectobyid(id);
        return ResponseEntity.ok(proyecto);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("proyecto")
    @ApiOperation(value = "Ingresa un nuevo registro a la BD")
    public ResponseEntity<ProyectosDTO> nuevoProy(@Valid ProyectosDTO proyDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getSubtitulo() == null) {
            return new ResponseEntity(new Mensaje("El subtitulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_demo() == null) {
            return new ResponseEntity(new Mensaje("El link de demo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_github() == null) {
            return new ResponseEntity(new Mensaje("El link de github es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_demo().length() > 255) {
            return new ResponseEntity(new Mensaje("El link de demo es demasiado largo"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_github().length() > 255) {
            return new ResponseEntity(new Mensaje("El link de github es demasiado largo"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Proyectos proy = new Proyectos();


        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                proy.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                proy.setImagenUrl(urlIMG);
                proy.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }

        }


        proy.setTitulo(proyDto.getTitulo());
        proy.setDescripcion(proyDto.getDescripcion());
        proy.setSubtitulo(proyDto.getSubtitulo());
        String dayhoy = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        proy.setCreado(dayhoy);
        proy.setCreatedAt(LocalDateTime.now());
        proy.setEditedAt(LocalDateTime.now());
        proy.setLink_demo(proyDto.getLink_demo());
        proy.setLink_github(proyDto.getLink_github());
        proyectosService.guardar(proy);

        return new ResponseEntity(new Mensaje("Proyecto creado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("proyecto/{id}/editar")
    @ApiOperation(value = "Actualiza un registro en la BD segun su id")
    public ResponseEntity<ProyectosDTO> editarHeader(@PathVariable("id") Integer id, @Valid ProyectosDTO proyDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {

        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }


        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getSubtitulo() == null) {
            return new ResponseEntity(new Mensaje("El subtitulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_demo() == null) {
            return new ResponseEntity(new Mensaje("El link de demo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_github() == null) {
            return new ResponseEntity(new Mensaje("El link de github es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_demo().length() > 255) {
            return new ResponseEntity(new Mensaje("El link de demo es demasiado largo"), HttpStatus.BAD_REQUEST);
        }

        if (proyDto.getLink_github().length() > 255) {
            return new ResponseEntity(new Mensaje("El link de github es demasiado largo"), HttpStatus.BAD_REQUEST);
        }

        // comprobar si el id es valido
        if (!proyectosService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Proyectos proy = new Proyectos();
        proy.setId(proyectosService.getProyectobyid(id).getId());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        String imgNAME = proyectosService.getProyectobyid(id).getImagenName();
        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Se elimino imagen para luego subir la nueva");
        }
        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                proy.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                proy.setImagenUrl(urlIMG);
                proy.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        proy.setTitulo(proyDto.getTitulo());
        proy.setDescripcion(proyDto.getDescripcion());
        proy.setCreatedAt(proyectosService.getProyectobyid(id).getCreatedAt());
        proy.setCreado(proyectosService.getProyectobyid(id).getCreado());
        proy.setLink_demo(proyDto.getLink_demo());
        proy.setLink_github(proyDto.getLink_github());
        proy.setEditedAt(LocalDateTime.now());
        proy.setSubtitulo(proyDto.getSubtitulo());

        proyectosService.guardar(proy);
        return new ResponseEntity(new Mensaje("Se edito correctamente"), HttpStatus.OK);

    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("proyecto/{id}/borrar")
    @ApiOperation(value = "Borra un registro en la BD segun su id")
    public ResponseEntity<Integer> borrar(@PathVariable("id") Integer id) throws IOException, Exception {

        // comprobar si el id es valido
        if (!proyectosService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Proyectos proy = proyectosService.getProyectobyid(id);
        String imgNAME = proy.getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }

        proyectosService.borrar(id);
        return new ResponseEntity(new Mensaje("El proyecto fue eliminado"), HttpStatus.OK);

    }

}
