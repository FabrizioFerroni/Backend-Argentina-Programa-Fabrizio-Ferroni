package com.herokuapp.apportfoliobackend.fabriziodev.controller;


import com.herokuapp.apportfoliobackend.fabriziodev.dto.HeaderDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.vm.Asset;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.HeaderService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

import org.apache.commons.io.FilenameUtils;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/")
public class HeaderController {

    @Value("${web.upload-path-images-header}")
    private String controllerPath;
    @Autowired
    HeaderService headerService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    S3Service s3Service;


    @GetMapping("headers")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros de la BD")
    public ResponseEntity<List<Header>> listarTodoHeader() {
        List<Header> list = headerService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("header")
    @ResponseBody
    @ApiOperation(value = "Lista el ultimo registro de la BD")
    public ResponseEntity<Header> getHeaderbyid() throws Exception {
        List<Header> header = headerService.getLastHeader();
        return new ResponseEntity(header, HttpStatus.OK);
    }

    @GetMapping("header/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista un registro de la BD segun su id")
    public ResponseEntity<Header> getHeaderbyid(@PathVariable("id") Integer id) throws Exception {
        Header header = headerService.getHeaderbyid(id);
        return new ResponseEntity(header, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("nuevo-header")
    @ApiOperation(value = "Inserta un nuevo registro en la BD")
    public ResponseEntity<HeaderDTO> nuevoHeader(@Valid HeaderDTO headerDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        if (headerDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (headerDto.getApellido() == null) {
            return new ResponseEntity(new Mensaje("El apellido es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (headerDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Header header = new Header();

        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                header.setImagenNAME(key);
                String urlIMG = s3Service.getUrlImg(key);
                header.setImagenURL(urlIMG);
                header.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }

        }


        header.setNombre(headerDto.getNombre());
        header.setApellido(headerDto.getApellido());
        header.setDescripcion(headerDto.getDescripcion());
        header.setCreatedAt(LocalDateTime.now());
        headerService.guardar(header);
        return new ResponseEntity(new Mensaje("Header creado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("header/{id}/editar")
    @ApiOperation(value = "Actualiza un registro de la BD segun su id")
    public ResponseEntity<HeaderDTO> editarHeader(@PathVariable("id") Integer id, @Valid HeaderDTO headDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        if (headDto.getNombre() == null) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (headDto.getApellido() == null) {
            return new ResponseEntity(new Mensaje("El apellido es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (headDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        // comprobar si el id es valido
        if (!headerService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Header head = new Header();
        head.setId(headerService.getHeaderbyid(id).getId());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        String imgNAME = headerService.getHeaderbyid(id).getImagenNAME();

        if (!(imgNAME == null)) {
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
                head.setImagenNAME(key);
                String urlIMG = s3Service.getUrlImg(key);
                head.setImagenURL(urlIMG);
                head.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        head.setNombre(headDto.getNombre());
        head.setApellido(headDto.getApellido());
        head.setDescripcion(headDto.getDescripcion());
        head.setCreatedAt(headerService.getHeaderbyid(id).getCreatedAt());
        head.setEditedAt(LocalDateTime.now());

        headerService.guardar(head);
        return new ResponseEntity(new Mensaje("Se edito correctamente"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("header/{id}/borrar")
    @ApiOperation(value = "Borra un registro de la BD segun su id")
    public ResponseEntity<Integer> borrar(@PathVariable("id") Integer id) throws IOException, Exception {

        // comprobar si el id es valido
        if (!headerService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Header head = headerService.getHeaderbyid(id);
        String imgNAME = head.getImagenNAME();

        if(imgNAME != null){
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }
            headerService.borrar(id);
            return new ResponseEntity(new Mensaje("El header fue eliminado"), HttpStatus.OK);

    }
}