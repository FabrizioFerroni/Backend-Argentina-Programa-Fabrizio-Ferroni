package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ContactoDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.EmailValuesDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.entity.Contacto;
import com.herokuapp.apportfoliobackend.fabriziodev.repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ContactoService {


    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mailContact}")
    private String mailTo;

    @Value("${spring.mail.host}")
    private String hostEmail;

    @Value("${spring.mail.username}")
    private String userEmail;

    @Value("${spring.mail.password}")
    private String passEmail;

    @Value("${spring.mail.name}")
    private String name;

    @Value("${hostfront.dns}")
    private String urlFront;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    ContactoRepository contactoRepository;


    /**
     * correo electrónico del remitente
     */
    private String from;

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    //Listar todos los contactos
    public List<Contacto> listarContactos() {
        return contactoRepository.findAllByOrderByIdDesc();
    }

    //Listar contacto por id
    public Contacto listarPorId(Integer id) throws Exception {
        Contacto contacto = contactoRepository.findById(id).orElseThrow(() -> new Exception("No se envio nigun contacto con este id"));
        return contacto;
    }

    public void save(Contacto contacto) {
        contactoRepository.save(contacto);
    }

    public void borrar(Integer id) {
        contactoRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return contactoRepository.existsById(id);
    }

    public void sendEmailContacto(ContactoDTO dto) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("nombre", dto.getNombre());
            model.put("apellido", dto.getApellido());
            model.put("email", dto.getEmail());
            model.put("asunto", dto.getSubject());
            model.put("mensaje", dto.getMensaje());
            if (dto.getTelefono() != null || dto.getTelefono() != "") {
                model.put("telefono", dto.getTelefono());
            }
            if (dto.getTelefono() == "" || dto.getTelefono() == null) {
                dto.setTelefono(null);
            }
            String dayhoy = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            String horahoy = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            String nameRem = dto.getNombre() + " " + dto.getApellido();
            String mailFrom2 = dto.getEmail();
            model.put("fecha", dayhoy);
            model.put("hora", horahoy);
            model.put("url", urlFront);
            context.setVariables(model);
            String htmlText = templateEngine.process("email-contact", context);
            InternetAddress ia=new InternetAddress(mailFrom2,nameRem);
            helper.setFrom(ia);
            helper.setCc(dto.getMailCc());
            helper.setTo(mailTo);
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);

            javaMailSender.send(message);
            sendEmailResp(dto);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendEmailResp(ContactoDTO dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("nombre", dto.getNombre());
            model.put("apellido", dto.getApellido());
            model.put("email", dto.getEmail());
            model.put("asunto", dto.getSubject());
            model.put("mensaje", dto.getMensaje());
            if (dto.getTelefono() != null || dto.getTelefono() != "") {
                model.put("telefono", dto.getTelefono());
            }
            if (dto.getTelefono() == "" || dto.getTelefono() == null) {
                dto.setTelefono(null);
            }
            context.setVariables(model);
            String htmlText = templateEngine.process("email-aviso", context);
            helper.setFrom(new InternetAddress(mailFrom, name));
            helper.setTo(dto.getEmail());
            helper.setSubject("Gracias por contactarte conmigo");
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }




}
