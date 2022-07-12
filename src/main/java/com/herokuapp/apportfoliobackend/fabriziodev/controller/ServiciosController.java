package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.ServiciosDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Servicios;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import com.herokuapp.apportfoliobackend.fabriziodev.service.ServiciosService;
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
public class ServiciosController {
    @Value("${web.upload-path-images-servicios}")
    private String controllerPath;

    @Autowired
    ServiciosService servService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    S3Service s3Service;

    @GetMapping("servicios")
    @ResponseBody
    @ApiOperation(value = "Listar todos los registros de la BD")
    public ResponseEntity<List<Servicios>> listarServicios() {
        List<Servicios> list = servService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("servicio/{id}")
    @ResponseBody
    @ApiOperation(value = "Listar registro de la BD segun su id")
    public ResponseEntity<Servicios> getServiciobyid(@PathVariable("id") Integer id) throws Exception {
        Servicios servicio = servService.getServiciosById(id);
        return new ResponseEntity(servicio, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("servicio")
    @ApiOperation(value = "Insertar un nuevo registro en la BD")
    public ResponseEntity<ServiciosDTO> guardarServicio(@Valid ServiciosDTO servDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }
        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al crear el header"), HttpStatus.BAD_REQUEST);
        }

        if (servDto.getTitulo() == null || servDto.getTitulo().isEmpty()) {
            return new ResponseEntity(new Mensaje("El titulo no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }

        if (servDto.getDescripcion() == null || servDto.getDescripcion().isEmpty()) {
            return new ResponseEntity(new Mensaje("La descripcion no puede estar vacia"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Servicios serv = new Servicios();

        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                serv.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                serv.setImagenUrl(urlIMG);
                serv.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }

        }

        serv.setTitulo(servDto.getTitulo());
        serv.setDescripcion(servDto.getDescripcion());
        serv.setCreatedAt(LocalDateTime.now());

        servService.guardar(serv);
        return new ResponseEntity(new Mensaje("Se ha guardado el servicio"), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("servicio/{id}/editar")
    @ApiOperation(value = "Actualiza un registro de la BD segun su id")
    public ResponseEntity<ServiciosDTO> editarServ(@PathVariable("id") Integer id, @Valid ServiciosDTO servDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        // comprobar si el id es valido
        if (!servService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }
        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al crear el servicio"), HttpStatus.BAD_REQUEST);
        }

        if (servDto.getTitulo() == null || servDto.getTitulo().isEmpty()) {
            return new ResponseEntity(new Mensaje("El titulo no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }

        if (servDto.getDescripcion() == null || servDto.getDescripcion().isEmpty()) {
            return new ResponseEntity(new Mensaje("La descripcion no puede estar vacia"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Servicios serv = new Servicios();

        serv.setId(servService.getServiciosById(id).getId());


        String imgName = servService.getServiciosById(id).getImagenName();
        if (imgName != null) {
            s3Service.deleteImage(imgName);
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
                serv.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                serv.setImagenUrl(urlIMG);
                serv.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        serv.setTitulo(servDto.getTitulo());
        serv.setDescripcion(servDto.getDescripcion());
        serv.setCreatedAt(servService.getServiciosById(id).getCreatedAt());
        serv.setEditedAt(LocalDateTime.now());

        servService.guardar(serv);
        return new ResponseEntity(new Mensaje("Se edito correctamente"), HttpStatus.OK);

    }


    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("/servicio/{id}/eliminar")
    @ApiOperation(value = "Borrar registro en la BD segun su id")
    public ResponseEntity<Integer> eliminarServicio(@PathVariable("id") Integer id) throws IOException, Exception {
        // comprobar si el id es valido
        if (!servService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Servicios serv = servService.getServiciosById(id);
        String imgNAME = serv.getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }
        servService.borrar(id);
        return new ResponseEntity(new Mensaje("Se borro el documento"), HttpStatus.OK);
    }

}


