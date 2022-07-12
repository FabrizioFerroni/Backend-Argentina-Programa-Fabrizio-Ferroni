package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.AcercaDeHomeDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDeHome;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.AcercaDeHomeService;
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
public class AcercaDeHomeController {

    @Value("${web.upload-path-images-acercadehome}")
    private String controllerPath;

    @Autowired
    S3Service s3Service;

    @Autowired
    AcercaDeHomeService acercaDeHomeService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/acercadehome")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros")
    public ResponseEntity<List<AcercaDeHome>> listarTodos() {
        List<AcercaDeHome> acercaDeHomeList = acercaDeHomeService.listarTodos();
        return new ResponseEntity<>(acercaDeHomeList, HttpStatus.OK);
    }

    @GetMapping("acdh")
    @ResponseBody
    @ApiOperation(value = "Lista el ultimo registro añadido a la BD")
    public ResponseEntity<AcercaDeHome> getAcdhbyid() throws Exception {
        List<AcercaDeHome> acdh = acercaDeHomeService.getLastAcdh();
        return new ResponseEntity(acdh, HttpStatus.OK);
    }

    @GetMapping("/acercadehome/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista un registro por id")
    public ResponseEntity<AcercaDeHome> getAcercaDeHomeById(@PathVariable Integer id) throws Exception {
        AcercaDeHome acercaDeHome = acercaDeHomeService.getAcercaDeHomeById(id);
        if (acercaDeHome == null) {
            throw new Exception("No se encontró el registro con id: " + id);
        }
        return new ResponseEntity<>(acercaDeHome, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("create/acdh")
    @ApiOperation(value = "Inserta un nuevo registro a la BD")
    public ResponseEntity<AcercaDeHomeDTO> nuevo(@Valid AcercaDeHomeDTO acercaDeHomeDTO, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        AcercaDeHome acdh = new AcercaDeHome();


        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                acdh.setImagenNAME(key);
                String urlIMG = s3Service.getUrlImg(key);
                acdh.setImagenURL(urlIMG);
                acdh.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);

            }

        }


        acdh.setTitulo(acercaDeHomeDTO.getTitulo());
        acdh.setDescripcion(acercaDeHomeDTO.getDescripcion());
        acdh.setDescripcion2(acercaDeHomeDTO.getDescripcion2());
        acdh.setLink(acercaDeHomeDTO.getLink());
        acdh.setCreatedAt(LocalDateTime.now());

        acercaDeHomeService.guardar(acdh);
        return new ResponseEntity(new Mensaje("Se ha guardado correctamente"), HttpStatus.OK);

    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("/acercadehome/{id}/editar")
    @ApiOperation(value = "Actualiza un registro segun el id obtenido")
    public ResponseEntity<AcercaDeHomeDTO> editACDH(@PathVariable("id") Integer id, @Valid AcercaDeHomeDTO acdhDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();


        AcercaDeHome acdh = new AcercaDeHome();
        acdh.setId(acercaDeHomeService.getAcercaDeHomeById(id).getId());

        if (!acercaDeHomeService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ninguna imagen"), HttpStatus.BAD_REQUEST);
        }

        if (acdhDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (acdhDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (acdhDto.getDescripcion2() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (acdhDto.getLink() == null) {
            return new ResponseEntity(new Mensaje("El link es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        String imgNAME = acercaDeHomeService.getAcercaDeHomeById(id).getImagenNAME();

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
                acdh.setImagenNAME(key);
                String urlIMG = s3Service.getUrlImg(key);
                acdh.setImagenURL(urlIMG);
                acdh.setUsuario_id(usuario.getId());
            } else {
                System.out.println(ext);
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        acdh.setTitulo(acdhDto.getTitulo());
        acdh.setDescripcion(acdhDto.getDescripcion());
        acdh.setDescripcion2(acdhDto.getDescripcion2());
        acdh.setLink(acdhDto.getLink());
        acdh.setCreatedAt(acercaDeHomeService.getAcercaDeHomeById(id).getCreatedAt());
        acdh.setEditedAt(LocalDateTime.now());

        acercaDeHomeService.guardar(acdh);
        return new ResponseEntity(new Mensaje("Se edito correctamente"), HttpStatus.OK);

    }


    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("acercadehome/{id}/borrar")
    @ApiOperation(value = "Borra de la BD el registro buscado por id")
    public ResponseEntity<Integer> borrar(@PathVariable("id") Integer id) throws IOException, Exception {

        // comprobar si el id es valido
        if (!acercaDeHomeService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        AcercaDeHome acdh = acercaDeHomeService.getAcercaDeHomeById(id);
        String imgNAME = acdh.getImagenNAME();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }

        acercaDeHomeService.borrar(id);
        return new ResponseEntity(new Mensaje("Se elimino con éxito"), HttpStatus.OK);

    }

}
