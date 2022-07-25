package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.DownloadCvDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.DownloadCv;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.CvService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.DownloadCvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/")
@ApiIgnore
public class DownloadCvController {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ROOT);
    }

    @Autowired
    DownloadCvService downloadCvService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CvService cvService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("downloadCv")
    @ResponseBody
    @ApiIgnore
    public  ResponseEntity<List<DownloadCv>> listartodos() {
        List<DownloadCv> list = downloadCvService.listarTodos();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("download_Cv/{id}")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<DownloadCv> getDownloadCvById(@PathVariable("id") Integer id) throws Exception {
        DownloadCv downloadCv = downloadCvService.getDownloadCvById(id);
        return new ResponseEntity(downloadCv, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("downloadCv/user/{id}")
    @ResponseBody
    public ResponseEntity<DownloadCv> getDownloadCvByUser(@PathVariable("id") Integer id) throws Exception {
//        DownloadCv downloadCv = downloadCvService.GetDownloadCvByUser(id);
        List<DownloadCv> user_dcv = downloadCvService.GetDownloadCvByUser(id);
        return new ResponseEntity(user_dcv, HttpStatus.OK);

    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("downloadCv/{id}")

    public ResponseEntity<DownloadCvDTO> nuevo(@PathVariable("id") Integer id, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        DownloadCv downloadCv = new DownloadCv();
        downloadCv.setNombre(usuario.getNombre());
        downloadCv.setNombreCv(cvService.listarPorId(id).getNombreCv());
        String hoy = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        downloadCv.setFDescarga(hoy);
        downloadCv.setRuta(cvService.listarPorId(id).getCvURL());
        String nomDesc = toSlug(cvService.listarPorId(id).getNombreCv()) + ".pdf";
        downloadCv.setNombredown(nomDesc);
        downloadCv.setApellido(usuario.getApellido());
        downloadCv.setEmail(usuario.getEmail());
        downloadCv.setCv_id(id);
        downloadCv.setUsuario_id(usuario.getId());
        downloadCv.setCreatedAt(LocalDateTime.now());
        downloadCvService.guardar(downloadCv);

        return new ResponseEntity(new Mensaje("Se descargo con exito el cv"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("downloadCv/{id}/eliminar")
    @ApiIgnore
    public ResponseEntity<Integer> eliminar(@PathVariable("id") Integer id) throws Exception {
        if (!downloadCvService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }
        downloadCvService.borrar(id);
        return new ResponseEntity(new Mensaje("Se elimino con exito el registro de quien descargo el cv"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("downloadCv/{id}/eliminarcv")
    @ApiIgnore
    public ResponseEntity<Integer> eliminarcv(@PathVariable("id") Integer id) throws Exception {
        if (!downloadCvService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.BAD_REQUEST);
        }
        downloadCvService.borrardown_cv(id);
        return new ResponseEntity(new Mensaje("Se elimino con exito los registros de quien descargo los cv"), HttpStatus.OK);
    }


}
