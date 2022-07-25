package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.CvDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Cv;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Header;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.CvService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/")
public class CvController {
    @Value("${web.upload-path-docs}")
    private String controllerPath;

    @Value("${host.dns}")
    private String host;

    @Autowired
    S3Service s3Service;

    @Autowired
    CvService cvService;

    @Autowired
    UsuarioService usuarioService;


    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ROOT);
    }

    @GetMapping("cvs")
    @ResponseBody
    @ApiOperation(value = "Lista todos los registros que hay en la BD")
    public ResponseEntity<List<Cv>> listarTodoHeader() {
        List<Cv> list = cvService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("cv/{id}")
    @ResponseBody
    @ApiOperation(value = "Lista un registro de la BD por el id")
    public ResponseEntity<Cv> listarPorId(@PathVariable("id") Integer id) throws Exception {
        Cv cv = cvService.listarPorId(id);
        if (cv == null) {
            throw new Exception("No se encontro el cv con id: " + id);
        }
        return new ResponseEntity<>(cv, HttpStatus.OK);
    }

    @GetMapping("cv")
    @ResponseBody
    @ApiOperation(value = "Obtiene el ultimo curriculum vitae subido a la BD")
    public ResponseEntity<Cv> getLastCv() throws Exception {
        List<Cv> cv = cvService.getLastCv();
        return new ResponseEntity(cv, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("cargar-cv")
    @ApiIgnore
    public ResponseEntity<CvDTO> nuevoCv(@Valid CvDTO cvDto, BindingResult result, @RequestParam("file") MultipartFile dcv, Authentication authentication) throws IOException, Exception {

        if (dcv.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ningún Cv"), HttpStatus.BAD_REQUEST);
        }
        if (dcv.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("El archivo es demasiado grande"), HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hubo un error al subir el cv"), HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Cv cv = new Cv();

        if (!dcv.isEmpty()) {
            String ext = FilenameUtils.getExtension(dcv.getOriginalFilename());
            if (ext.equals("pdf") || ext.equals("docx") || ext.equals("doc")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(dcv, nFn);
                cv.setCvNAME(key);
//                String urlIMG = s3Service.getUrlImg(key);
                String urlIMG = host + "/file/download/" + key;
                cv.setCvURL(urlIMG);
                cv.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: PDF, DOCX, DOC. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);

            }

        }

        cv.setNombreCv(cvDto.getNombreCv());
        cv.setDescripcionCv(cvDto.getDescripcionCv());
        String nomDesc = toSlug(cvDto.getNombreCv()) + ".pdf";
        cv.setNombredown(nomDesc);
        cv.setCreatedAt(LocalDateTime.now());
        cvService.guardar(cv);

        return new ResponseEntity(new Mensaje("Cv subido correctamente"), HttpStatus.OK);
    }

    //cv dto


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("cv/{id}/editar")
    @ApiIgnore
    public ResponseEntity<CvDTO> actualizarCv(@PathVariable("id") Integer id,
                                              @Valid CvDTO cvDto,
                                              @RequestParam("file") MultipartFile dcv,
                                              Authentication authentication) throws IOException, Exception {

        if (dcv.isEmpty()) {
            return new ResponseEntity(new Mensaje("No se ha seleccionado ningún Cv"), HttpStatus.BAD_REQUEST);
        }
        /*if (dcv.getSize() > 5000000) {
            return new ResponseEntity(new Mensaje("El archivo es demasiado grande"), HttpStatus.BAD_REQUEST);
        }*/

        // comprobar si el id es valido
        if (!cvService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Cv cv = new Cv();

        cv.setId(cvService.listarPorId(id).getId());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();
        String cvNAME = cvService.listarPorId(id).getCvNAME();


        if (cvNAME != null) {
            s3Service.deleteImage(cvNAME);
            System.out.println("CV Eliminado");
        }
        if (!dcv.isEmpty()) {
            String ext = FilenameUtils.getExtension(dcv.getOriginalFilename());
            if (ext.equals("pdf") || ext.equals("docx") || ext.equals("doc")) {
                Random random = new Random();
                int r = random.nextInt(999);
                String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(Calendar.getInstance().getTime());
                String nFn = controllerPath + r + "_" + timeStamp + "_" + usuario.getId() + "_" + usuario.getNombreUsuario();
                String key = s3Service.putObject(dcv, nFn);
                cv.setCvNAME(key);
                String urlIMG = host + "/file/download/" + key;
                cv.setCvURL(urlIMG);
                cv.setUsuario_id(usuario.getId());
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: PDF, DOCX, DOC. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }

        cv.setNombreCv(cvDto.getNombreCv());
        cv.setDescripcionCv(cvDto.getDescripcionCv());
        String nomDesc = toSlug(cvDto.getNombreCv()) + ".pdf";
        cv.setNombredown(nomDesc);
        cv.setCreatedAt(cvService.listarPorId(id).getCreatedAt());
        cv.setEditedAt(LocalDateTime.now());
        cvService.guardar(cv);
        return new ResponseEntity(new Mensaje("Se edito correctamente el cv"), HttpStatus.OK);


    }


    @GetMapping("test/{id}")
    @ApiIgnore
    public ResponseEntity<?> test(@PathVariable("id") Integer id){
        return new ResponseEntity(cvService.existsById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("cv/{id}/eliminar")
    @ApiIgnore
    public ResponseEntity<Integer> eliminarCv(@PathVariable("id") Integer id) throws Exception {
        // comprobar si el id es valido
        if (!cvService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }

        Cv cv = cvService.listarPorId(id);
        String cvNAME = cv.getCvNAME();

        if (cvNAME != null) {
            s3Service.deleteImage(cvNAME);
            System.out.println("CV Eliminado");
        }

        cvService.borrar(id);
        cvService.borrardown_cv(id);
        return new ResponseEntity(new Mensaje("Se borro el cv"), HttpStatus.OK);

    }
}


