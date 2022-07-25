package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.ChangePasswordDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.EmailValuesDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Suscripcion;
import com.herokuapp.apportfoliobackend.fabriziodev.service.EmailService;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/")
public class EmailController {
    @Autowired
    EmailService emailService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final String subject = "Cambio de Contraseña";

    @PostMapping("send-email")
    @ApiOperation(value = "Metodo para enviar el correo para cambiar de contraseña de usuario")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioService.getByNombreUsuarioOrEmail(dto.getMailTo());
        if(!usuarioOpt.isPresent())
            return new ResponseEntity(new Mensaje("No se encontro ningun usuario valido"), HttpStatus.NOT_FOUND);
        Usuario usuario = usuarioOpt.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(usuario.getEmail());
        dto.setSubject(subject);
        String userNamelast = usuario.getNombre() + " " + usuario.getApellido();
        dto.setUserName(userNamelast);
        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        usuario.setTokenPassword(tokenPassword);

        usuario.setCaducidadToken(LocalDateTime.now());
        dto.setCaducidadToken(LocalDateTime.now());
        usuarioService.save(usuario);
        emailService.sendEmail(dto);
        return new ResponseEntity(new Mensaje("Te hemos enviado un correo con los pasos a seguir para cambiar la clave"), HttpStatus.OK);
    }

    @GetMapping("getidwithtoken_user/{tokensub}")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<Map<String, Object>> getidwithtoken(@PathVariable String tokensub) {
        HashMap<String, Object> map = new HashMap<>();
        Optional<Usuario> userOpt = usuarioService.getByTokenPassword(tokensub);

        if (!userOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("El token no existe o ya fue cambiada la clave"), HttpStatus.NOT_FOUND);
        }

        Usuario user = new Usuario();
        user.setId(userOpt.get().getId());

        int id = userOpt.get().getId();
        String username = userOpt.get().getNombreUsuario();
        String token = userOpt.get().getTokenPassword();
        LocalDateTime creado = userOpt.get().getCaducidadToken();

        if (!usuarioService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id del token no existe"), HttpStatus.NOT_FOUND);
        }

        map.put("id_user", id);
        map.put("username", username);
        map.put("token", token);
        map.put("creado", creado);

        return new ResponseEntity(map, HttpStatus.OK);
    }


  @GetMapping("borrartoken/{token}")
  @ResponseBody
  @ApiIgnore
  public ResponseEntity<?> deletewithtoken(@PathVariable String token) {
      Optional<Usuario> userOpt = usuarioService.getByTokenPassword(token);

      if (!userOpt.isPresent()) {
          return new ResponseEntity(new Mensaje("El token no existe o ya fue cambiada la clave"), HttpStatus.NOT_FOUND);
      }

      Usuario user = new Usuario();
      user.setId(userOpt.get().getId());
      user.setNombre(userOpt.get().getNombre());
      user.setApellido(userOpt.get().getApellido());
      user.setEmail(userOpt.get().getEmail());
      user.setNombreUsuario(userOpt.get().getNombreUsuario());
      user.setPassword(userOpt.get().getPassword());
      user.setAvatar(userOpt.get().getAvatar());
      user.setCreatedAt(userOpt.get().getCreatedAt());
      user.setEditedAt(userOpt.get().getEditedAt());
      user.setImagenName(userOpt.get().getImagenName());
      user.setRoles(userOpt.get().getRoles());
      user.setCaducidadToken(null);
      user.setTokenPassword(null);
      usuarioService.save(user);

      return new ResponseEntity("Token no valido", HttpStatus.OK);
  }
    @PostMapping("/change-password")
    @ApiOperation(value = "Cambia y guarda la nueva contraseña en la BD y te envia un correo de confirmacion de cambio")
    public ResponseEntity<ChangePasswordDTO> changePassword(@Valid @RequestBody ChangePasswordDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("Campos mal puestos"), HttpStatus.BAD_REQUEST);
        if(!dto.getPassword().equals(dto.getConfirmPassword()))
            return new ResponseEntity(new Mensaje("Las contraseñas no coinciden"), HttpStatus.BAD_REQUEST);
        Optional<Usuario> usuarioOpt = usuarioService.getByTokenPassword(dto.getTokenPassword());
        if(!usuarioOpt.isPresent())
            return new ResponseEntity(new Mensaje("No existe ningún usuario con este token o token no valido"), HttpStatus.NOT_FOUND);
        Usuario usuario = usuarioOpt.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(newPassword);
        usuario.setTokenPassword(null);
        usuario.setCaducidadToken(null);
        String name = usuario.getNombre();
        String mailTO = usuario.getEmail();
        String subject = name + ", su contraseña ya está actualizada";
        dto.setNombre(name);
        dto.setMailTo(mailTO);
        dto.setSubject(subject);
        usuarioService.save(usuario);
        emailService.sendEmailsucc(dto);
        return new ResponseEntity(new Mensaje("Contraseña actualizada con éxito"), HttpStatus.OK);
    }

}
