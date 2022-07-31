package com.herokuapp.apportfoliobackend.fabriziodev.security.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import com.herokuapp.apportfoliobackend.fabriziodev.security.dto.JwtDto;
import com.herokuapp.apportfoliobackend.fabriziodev.security.dto.LoginUsuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.dto.NuevoUsuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Rol;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.enums.RolNombre;
import com.herokuapp.apportfoliobackend.fabriziodev.security.jwt.JwtProvider;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.RolService;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.UsuarioService;
import com.herokuapp.apportfoliobackend.fabriziodev.service.S3Service;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/auth")
public class AuthController {

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    //    Variables
    @Value("${web.upload-path-images-usuarios}")
    private String controllerPath;

    @Value("${hostfront.dns}")
    private String host;

    @Autowired
    S3Service s3Service;

//    Fin variables

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/usuarios")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Lista todos los usuarios registrados")
    public ResponseEntity<Iterable<Usuario>> getUsuarios() {
        return new ResponseEntity(usuarioService.listarUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/usuario")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Lista los detalles del usuario")
    public ResponseEntity<?> user(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registrarse")
    @ApiOperation(value = "Registra un nuevo usuario en la BD")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(new Mensaje("Hay campos mal ingresados y/o vacíos, o ese email y/o usuario ya esta registrado"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            return new ResponseEntity(new Mensaje("Este usuario ya esta registrado en nuestra BD"), HttpStatus.BAD_REQUEST);
        }
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return new ResponseEntity(new Mensaje("Este email ya esta registrado en nuestra BD"), HttpStatus.BAD_REQUEST);
        }

        if (!isValid(nuevoUsuario.getEmail())) {
            return new ResponseEntity(new Mensaje("El email ingresado no es valido"), HttpStatus.BAD_REQUEST);
        }

        if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().isEmpty()) {
            return new ResponseEntity(new Mensaje("El nombre ingresado no es valido o esta vacío"), HttpStatus.BAD_REQUEST);
        }

        if (nuevoUsuario.getApellido() == null || nuevoUsuario.getApellido().isEmpty()) {
            return new ResponseEntity(new Mensaje("El apellido ingresado no es valido o esta vacío"), HttpStatus.BAD_REQUEST);
        }

        if (nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().isEmpty()) {
            return new ResponseEntity(new Mensaje("La contraseña ingresada no es valida o esta vacía"), HttpStatus.BAD_REQUEST);
        }

        if (nuevoUsuario.getNombreUsuario() == null || nuevoUsuario.getNombreUsuario().isEmpty()) {
            return new ResponseEntity(new Mensaje("El nombre de usuario ingresado no es valido o esta vacío"), HttpStatus.BAD_REQUEST);
        }

        if (nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().isEmpty()) {
            return new ResponseEntity(new Mensaje("El email ingresado no es valido o esta vacío"), HttpStatus.BAD_REQUEST);
        }


        if (nuevoUsuario.getPassword().length() < 8) {
            return new ResponseEntity(new Mensaje("La contraseña ingresado no es valida, debe tener al menos 8 caracteres"), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario
                = new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getApellido(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(),
                passwordEncoder.encode(nuevoUsuario.getPassword()), nuevoUsuario.getAvatar(), nuevoUsuario.getImagenName(), nuevoUsuario.getCreatedAt(), nuevoUsuario.getEditedAt());
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());

        if (nuevoUsuario.getRoles().contains("admin") || nuevoUsuario.getNombreUsuario().equals("fferroni")) {
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_PROFESOR).get());
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        }
        if (nuevoUsuario.getRoles().contains("profesor")) {
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_PROFESOR).get());
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        }
        usuario.setRoles(roles);

        String randomCode = RandomString.make(64);
        String url = host + "/iniciarsesion/verificarusuario/" + randomCode;
        usuario.setVerifyPassword(randomCode);
        usuario.setActiveUser(false);
        nuevoUsuario.setMailTo(nuevoUsuario.getEmail());
        nuevoUsuario.setNombre(nuevoUsuario.getNombre());
        nuevoUsuario.setApellido(nuevoUsuario.getApellido());
        nuevoUsuario.setNombreUsuario(nuevoUsuario.getNombreUsuario());
        nuevoUsuario.setUrlValidate(url);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setCaducidadToken(LocalDateTime.now());
        String subject = nuevoUsuario.getNombre() + ", verifica tu cuenta para poder ingresar a mi portafolio Web";
        nuevoUsuario.setSubject(subject);
        usuarioService.save(usuario);
        usuarioService.sendEmailreg(nuevoUsuario);
        System.out.println("Usuario roles: " + nuevoUsuario.getRoles());
        return new ResponseEntity(new Mensaje("Usuario registrado con éxito. Por favor verifica tu casilla de correo para verificar la cuenta"), HttpStatus.CREATED);
    }

    @GetMapping("getidwithtoken_user/{tokensub}")
    @ResponseBody
    @ApiIgnore
    public ResponseEntity<Map<String, Object>> getidwithtoken(@PathVariable String tokensub) {
        HashMap<String, Object> map = new HashMap<>();
        Optional<Usuario> userOpt = usuarioService.getByVerifyPassword(tokensub);

        if (!userOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("El token no existe o ya fue verificada la cuenta"), HttpStatus.NOT_FOUND);
        }

        /*Usuario user = new Usuario();
        user.setId(userOpt.get().getId());*/

        int id = userOpt.get().getId();
        String username = userOpt.get().getNombreUsuario();
        String token = userOpt.get().getVerifyPassword();
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


    @GetMapping("/verifyuser/{token}")
    @ApiIgnore
    ResponseEntity<?> activeuser(@PathVariable String token) {
        Optional<Usuario> userOpt = usuarioService.getByVerifyPassword(token);
        if (!userOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("El token no existe o ya fue verificada la cuenta"), HttpStatus.NOT_FOUND);
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
        user.setVerifyPassword(null);
        user.setActiveUser(true);

        usuarioService.save(user);
        usuarioService.sendEmailregver(user);

        return new ResponseEntity(new Mensaje("Cuenta verificada con éxito"), HttpStatus.OK);
    }

    @GetMapping("/resendtoken/{token}")
    @ApiIgnore
    ResponseEntity<?> reactiveuser(@PathVariable String token) {
        Optional<Usuario> userOpt = usuarioService.getByVerifyPassword(token);
        if (!userOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("El token no existe o ya fue verificada la cuenta"), HttpStatus.NOT_FOUND);
        }

        Usuario user = new Usuario();
        String randomCode = RandomString.make(64);
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
        user.setTokenPassword(null);
        user.setVerifyPassword(randomCode);
        user.setActiveUser(false);
        user.setCaducidadToken(LocalDateTime.now());

        usuarioService.save(user);
        usuarioService.sendEmailregver(user);

        return new ResponseEntity(new Mensaje("Se mando código de verificacion nuevo"), HttpStatus.OK);
    }

    @PostMapping("/iniciarsesion")
    @ApiOperation(value = "Metodo login para autenticar al usuario")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(new Mensaje("campos mal puestos"), HttpStatus.BAD_REQUEST);
        }

        if (loginUsuario.getNombreUsuario() == "" && loginUsuario.getPassword() == "") {
            return new ResponseEntity(new Mensaje("Por favor ingresar nombre de usuario y contraseña"), HttpStatus.BAD_REQUEST);
        }
        if (loginUsuario.getNombreUsuario() == null || loginUsuario.getNombreUsuario().isEmpty()) {
            return new ResponseEntity(new Mensaje("Por favor ingrese un nombre de usuario"), HttpStatus.BAD_REQUEST);
        }

        if (loginUsuario.getPassword() == null || loginUsuario.getPassword().isEmpty()) {
            return new ResponseEntity(new Mensaje("Por favor ingrese una contraseña"), HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!usuarioService.existsByNombreUsuario(loginUsuario.getNombreUsuario()) || !encoder.matches(loginUsuario.getPassword(), usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get().getPassword())) {
            return new ResponseEntity(new Mensaje("El nombre de usuario o la contraseña ingresada no son correctos"), HttpStatus.BAD_REQUEST);
        }

        if (usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get().isActiveUser()) {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);
            JwtDto jwtDto = new JwtDto(jwt);
            return new ResponseEntity(jwtDto, HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("La cuenta de usuario no fue verificada"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    @ApiOperation(value = "Refresca el token del usuario")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException {
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity(jwt, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("usuario/{id}/editar")
    @ApiOperation(value = "Actualizar los datos del usuario")
    public ResponseEntity<NuevoUsuario> edituser(@PathVariable Integer id, @Valid NuevoUsuario dto, BindingResult result, @RequestParam("file") MultipartFile imagen, Authentication authentication) throws IOException, Exception {
        if (!usuarioService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.NOT_FOUND);
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

        if (dto.getNombre() == null || dto.getNombre() == "") {
            return new ResponseEntity(new Mensaje("El nombre no puede estar vacio"), HttpStatus.NOT_FOUND);
        }

        if (dto.getApellido() == null || dto.getApellido() == "") {
            return new ResponseEntity(new Mensaje("El apellido no puede estar vacio"), HttpStatus.NOT_FOUND);
        }

        if (dto.getEmail() == null || dto.getEmail() == "") {
            return new ResponseEntity(new Mensaje("El email no puede estar vacio"), HttpStatus.NOT_FOUND);
        }

        if (dto.getNombreUsuario() == null || dto.getNombreUsuario() == "") {
            return new ResponseEntity(new Mensaje("El nombre de usuario no puede estar vacio"), HttpStatus.NOT_FOUND);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioService.getByNombreUsuario(userDetails.getUsername()).get();

        Usuario user = new Usuario();
        user.setId(usuarioService.getUserbyid(id).getId());

        String imgName = usuarioService.getUserbyid(id).getImagenName();

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
                user.setImagenName(key);
                String urlIMG = s3Service.getUrlImg(key);
                user.setAvatar(urlIMG);
            } else {
                return new ResponseEntity(new Mensaje("Archivos no soportados por el servidor. Los archivos deberan ser del formato: BMP, GIF, JPG, JPEG, PNG, SVG, WEBP. \nEstas mandando un archivo de esta extension: ." + ext), HttpStatus.BAD_REQUEST);
            }
        }
        user.setNombre(dto.getNombre());
        user.setApellido(dto.getApellido());
        user.setEmail(dto.getEmail());
        user.setNombreUsuario(usuarioService.getUserbyid(id).getNombreUsuario());
        user.setPassword(usuarioService.getUserbyid(id).getPassword());
        user.setCreatedAt(usuarioService.getUserbyid(id).getCreatedAt());
        user.setEditedAt(LocalDateTime.now());
        user.setRoles(usuarioService.getUserbyid(id).getRoles());
        user.setTokenPassword(usuarioService.getUserbyid(id).getTokenPassword());
        user.setCaducidadToken(usuarioService.getUserbyid(id).getCaducidadToken());
        user.setVerifyPassword(usuarioService.getUserbyid(id).getVerifyPassword());
        user.setActiveUser(usuarioService.getUserbyid(id).isActiveUser()); //true

        usuarioService.save(user);
        return new ResponseEntity(new Mensaje("Se edito correctamente el usuario"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("usuario/{id}/borrar")
    @ApiOperation(value = "Dar de baja la cuenta del usuario")
    public ResponseEntity<Integer> deleteuser(@PathVariable Integer id) throws IOException, Exception {
        if (!usuarioService.existsById(id)) {
            return new ResponseEntity(new Mensaje("El id no es valido"), HttpStatus.NOT_FOUND);
        }

        Usuario user = usuarioService.getUserbyid(id);
        String imgNAME = user.getImagenName();

        if (imgNAME != null) {
            s3Service.deleteImage(imgNAME);
            System.out.println("Imagen eliminada");
        }

        usuarioService.borrar(id);
        return new ResponseEntity(new Mensaje("Se borro el usuario"), HttpStatus.OK);
    }
}
