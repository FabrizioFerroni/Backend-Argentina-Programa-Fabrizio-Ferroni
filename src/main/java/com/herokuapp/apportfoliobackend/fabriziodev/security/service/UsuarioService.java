package com.herokuapp.apportfoliobackend.fabriziodev.security.service;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ChangePasswordDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Suscripcion;
import com.herokuapp.apportfoliobackend.fabriziodev.security.dto.NuevoUsuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Usuario;
import com.herokuapp.apportfoliobackend.fabriziodev.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Value("${hostfront.dns}")
    private String urlFront;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.name}")
    private String name;


    //Listar todos los usuarios
    public Iterable<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUserbyid(Integer id) throws Exception {
        Usuario user = usuarioRepository.findById(id).orElseThrow(() -> new Exception("El usuario buscado no existe"));
        return user;
    }
    public Optional<Usuario> getByNombreUsuario(String nombreUsuario){
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    public Optional<Usuario> getByNombreUsuarioOrEmail(String nombreOrEmail){
        return usuarioRepository.findByNombreUsuarioOrEmail(nombreOrEmail, nombreOrEmail);
    }

    public Optional<Usuario> getByTokenPassword(String tokenPassword){
        return usuarioRepository.findByTokenPassword(tokenPassword);
    }

    public Optional<Usuario> getByVerifyPassword(String verifyPassword){
        return usuarioRepository.findByverifyPassword(verifyPassword);
    }


    public boolean existsByNombreUsuario(String nombreUsuario){
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

  /*  public boolean existsByVerfifyUser(boolean activeUser){
        return usuarioRepository.isActiveUser_verify(activeUser);
    }*/

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public void borrar(Integer id){ usuarioRepository.deleteById(id); }

    public boolean existsById(Integer id) {
        return usuarioRepository.existsById(id);
    }


    public void sendEmailreg(NuevoUsuario dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("nombre", dto.getNombre());
            model.put("apellido", dto.getApellido());
            model.put("url", dto.getUrlValidate());
            context.setVariables(model);
            String htmlText = templateEngine.process("register-template", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendEmailregver(Usuario dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            String url = urlFront + "/iniciarsesion";
            String subject = dto.getNombre() + ", te has registrado con éxito en mi portfolio web";
            model.put("nombre", dto.getNombre());
            model.put("apellido", dto.getApellido());
            model.put("email", dto.getEmail());
            model.put("userName", dto.getNombreUsuario());
            model.put("url", url);
            context.setVariables(model);
            String htmlText = templateEngine.process("verify-complete", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ressendEmailreg(Usuario dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            String url = urlFront + "/iniciarsesion/verificarusuario/" + dto.getVerifyPassword();
            String subject = dto.getNombre() + ", nuevo codigo de verificacion.";
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("nombre", dto.getNombre());
            model.put("apellido", dto.getApellido());
            model.put("url", url);
            context.setVariables(model);
            String htmlText = templateEngine.process("register-template", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
