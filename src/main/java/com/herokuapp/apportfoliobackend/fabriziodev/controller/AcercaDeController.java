package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.AcercaDeDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.AcercaDe;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.AcercaDeService;
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
public class AcercaDeController {

    @Value("${web.upload-path-images-acercade}")
    private String controllerPath;

    @Autowired
    S3Service s3Service;

    @Autowired
    AcercaDeService acercaDeService;

    @Autowired
    UsuarioService usuarioService;

    @ApiOperation(value = "Obtiene todos los registros de acerca de")
    @GetMapping("/acercade")
    @ResponseBody
    public ResponseEntity<List<AcercaDe>> listarTodos() {
        List<AcercaDe> acercaDe = acercaDeService.listarTodos();
        return new ResponseEntity<>(acercaDe, HttpStatus.OK);
    }

    @GetMapping("acd")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros")
    public ResponseEntity<AcercaDe> getAcdbyid() throws Exception {
        List<AcercaDe> acd = acercaDeService.getLastAcd();
        return new ResponseEntity(acd, HttpStatus.OK);
    }

    @ApiOperation(value = "Obtiene el registro de acerca de según el id")
    @GetMapping("/acercade/{id}")
    @ResponseBody
    public ResponseEntity<AcercaDe> getAcercaDeById(@PathVariable Integer id) throws Exception {
        AcercaDe acercaDe = acercaDeService.getAcercaDeById(id);
        if (acercaDe == null) {
            throw new Exception("No se encontró el registro con id: " + id);
        }
        return new ResponseEntity(acercaDe, HttpStatus.OK);
    }

    @ApiOperation(value = "Crea un registro de acerca de")
    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping("/acercade")
    public ResponseEntity<AcercaDeDTO> nuevo(@Valid AcercaDeDTO acercaDeDTO, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (acercaDeDTO.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (acercaDeDTO.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        AcercaDe acd = new AcercaDe();

        if (!imagen.isEmpty()) {
            String ext = FilenameUtils.getExtension(imagen.getOriginalFilename());
            if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("svg") || ext.equals("gif") || ext.equals("bmp") || ext.equals("webp")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(imagen, nFn);
                acd.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                acd.setImagenUrl(urlIMG);
                acd.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);

            }

        }


        acd.setTitulo(acercaDeDTO.getTitulo());
        acd.setDescripcion(acercaDeDTO.getDescripcion());
        acd.setEmail(acercaDeDTO.getEmail());
        acd.setCreatedAt(LocalDateTime.now());

        acercaDeService.save(acd);
        return new ResponseEntity(new Mensaje("Se ha guardado correctamente"), HttpStatus.OK);

    }


    @ApiOperation(value = "Actualiza un registro de acerca de")
    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("/acercade/{id}/editar")
    public ResponseEntity<AcercaDeDTO> editACD(@PathVariable("id") Integer id, @Valid AcercaDeDTO acdDto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (acdDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (acdDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();


        AcercaDe acd = new AcercaDe();
        acd.setId(acercaDeService.getAcercaDeById(id).getId());

        if (!acercaDeService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el acerca de home"), HttpStatus.BAD_REQUEST);
        }

        if (acdDto.getTitulo() == null) {
            return new ResponseEntity(new Mensaje("El titulo es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (acdDto.getDescripcion() == null) {
            return new ResponseEntity(new Mensaje("La descripcion es obligatoria"), HttpStatus.BAD_REQUEST);
        }

        if (acdDto.getEmail() == null) {
            return new ResponseEntity(new Mensaje("El email es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        if (imagen.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("La imagen es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        String imgNAME = acercaDeService.getAcercaDeById(id).getImagenName();

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
                acd.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                acd.setImagenUrl(urlIMG);
                acd.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        acd.setTitulo(acdDto.getTitulo());
        acd.setDescripcion(acdDto.getDescripcion());
        acd.setEmail(acdDto.getEmail());
        acd.setCreatedAt(acercaDeService.getAcercaDeById(id).getCreatedAt());
        acd.setEditedAt(LocalDateTime.now());

        acercaDeService.save(acd);
        return new ResponseEntity(new Mensaje("Se edito correctamente"), HttpStatus.OK);

    }

    @ApiOperation(value = "Elimina un acerca de de la base de datos")
    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("acercade/{id}/borrar")
    public ResponseEntity<Integer> borrar(@PathVariable("id") Integer id) throws IOException, Exception {

        // comprobar si el id es valido
        if (!acercaDeService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        AcercaDe acd = acercaDeService.getAcercaDeById(id);
        String imgNAME = acd.getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }

        acercaDeService.delete(id);
        return new ResponseEntity(new Mensaje("Se borro el documento"), HttpStatus.OK);
    }
}
