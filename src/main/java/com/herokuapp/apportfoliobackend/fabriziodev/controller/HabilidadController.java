package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.HabilidadDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Habilidad;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.HabilidadService;
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

@RestController
@RequestMapping("/api/")
public class HabilidadController {

    @Value("${web.upload-path-images-habilidad}")
    private String controllerPath;

    @Autowired
    S3Service s3Service;

    @Autowired
    HabilidadService habilidadService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("habilidades")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros que hay en la BD")
    public ResponseEntity<List<Habilidad>> listarTodos() {
        List<Habilidad> habilidades = habilidadService.findAll();
        return new ResponseEntity<List<Habilidad>>(habilidades, HttpStatus.OK);
    }

    @GetMapping("habilidad/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista el registro de la BD segun su id")
    public ResponseEntity<Habilidad> getHabilidadById(@PathVariable Integer id) throws Exception {
        Habilidad habilidad = habilidadService.getHabilidadById(id);
        if (habilidad == null) {
            throw new Exception("No se encontró la habilidad con id: " + id);
        }
        return new ResponseEntity<Habilidad>(habilidad, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("habilidad")
    @ApiOperation(value = "Inserta un nuevo registro a la BD")
    public ResponseEntity<HabilidadDTO> nuevo(@Valid HabilidadDTO habDTO, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (habDTO.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (habDTO.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Habilidad hab = new Habilidad();


        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                hab.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                hab.setImagenUrl(urlIMG);
                hab.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);

            }

        }


        hab.setTitulo(habDTO.getTitulo());
        hab.setDescripcion(habDTO.getDescripcion());
        hab.setCreatedAt(LocalDateTime.now());

        habilidadService.guardar(hab);
        return new ResponseEntity(new Mensaje("Se ha guardado correctamente"), HttpStatus.OK);

    }


    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("habilidad/{id}/editar")
    @ApiOperation(value = "Actualiza un registro de la BD segun su id")
    public ResponseEntity<HabilidadDTO> editACDH(@PathVariable("id") Integer id, @Valid HabilidadDTO habDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();


        Habilidad hab = new Habilidad();
        hab.setId(habilidadService.getHabilidadById(id).getId());

        if (!habilidadService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (habDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (habDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        String imgNAME = habilidadService.getHabilidadById(id).getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }

        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                hab.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                hab.setImagenUrl(urlIMG);
                hab.setUsuario_id(usuario.getId());
            } else {
                System.out.println(ext);
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        hab.setTitulo(habDto.getTitulo());
        hab.setDescripcion(habDto.getDescripcion());
        hab.setCreatedAt(habilidadService.getHabilidadById(id).getCreatedAt());
        hab.setEditedAt(LocalDateTime.now());

        habilidadService.guardar(hab);
        return new ResponseEntity(new Mensaje("Se edito correctamente la habilidad"), HttpStatus.OK);

    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("habilidad/{id}/borrar")
    @ApiOperation(value = "Borra un registro de la BD segun su id")
    public ResponseEntity<Integer> borrar(@PathVariable("id") Integer id) throws IOException, Exception {

        // comprobar si el id es valido
        if (!habilidadService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Habilidad hab = habilidadService.getHabilidadById(id);
        String imgNAME = hab.getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }
        habilidadService.borrar(id);
        return new ResponseEntity(new Mensaje("Se borro el documento"), HttpStatus.OK);
    }


}
