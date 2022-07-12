package com.herokuapp.apportfoliobackend.fabriziodev.service;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.ChangePasswordDTO;
import com.herokuapp.apportfoliobackend.fabriziodev.dto.EmailValuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mail.urlFront}")
    private String urlFront;


    @Value("${hostfront.dns}")
    private String urlFront2;
    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.name}")
    private String name;

    public void sendEmail(EmailValuesDTO dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("userName", dto.getUserName());
            model.put("url", urlFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template", context);
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


    public void sendEmailsucc(ChangePasswordDTO dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            String url = urlFront2 + "/mi-perfil";
            model.put("nombre", dto.getNombre());
            model.put("url", url);
            context.setVariables(model);
            String htmlText = templateEngine.process("email-ok-change", context);
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
}
