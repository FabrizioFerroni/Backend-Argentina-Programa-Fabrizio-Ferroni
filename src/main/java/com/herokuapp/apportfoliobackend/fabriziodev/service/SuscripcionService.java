package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.SuscripcionDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Suscripcion;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.SuscripcionRepository;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.herokuapp.apportfoliobackend.fabriziodev.security.dto.NuevoUsuario;
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

@Service
@Transactional
public class SuscripcionService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${hostfront.dns}")
    private String urlFront;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.name}")
    private String name;

    @Autowired
    SuscripcionRepository susRepository;

    public List<Suscripcion> listarTodos() {
        return susRepository.findAllByOrderByIdDesc();
    }

    public Suscripcion getSuscbyid(Integer id) throws Exception {
        Suscripcion suscripcion = susRepository.findById(id).orElseThrow(() -> new Exception("El suscriptor no existe"));
        return suscripcion;
    }

    public Optional<Suscripcion> getByTokenSub(String tokenSub){
        return susRepository.findByTokenSus(tokenSub);
    }

    public void guardar(Suscripcion suscripcion) {
        susRepository.save(suscripcion);
    }

    public void borrar(Integer id) {
        susRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return susRepository.existsById(id);
    }

    public void sendEmailSus(Suscripcion dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            String url = urlFront + "/darme-de-baja/" + dto.getTokenSus();
            model.put("nombre", dto.getNombre());
            model.put("url", url);
            context.setVariables(model);
            String htmlText = templateEngine.process("new-suscriber", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getEmail());
            String subject = dto.getNombre() + ", Gracias por suscribirte a mi boletín de noticias 😍";
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendEmailUnsus(Suscripcion dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            String url = urlFront;
            model.put("nombre", dto.getNombre());
            model.put("email", dto.getEmail());
            model.put("url", url);
            context.setVariables(model);
            String htmlText = templateEngine.process("confirm-unsuscribe", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getEmail());
            String subject = dto.getNombre() + ", ya no formas parte de mis suscriptores 🤞";
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
