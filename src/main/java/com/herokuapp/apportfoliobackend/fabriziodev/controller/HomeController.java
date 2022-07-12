package com.herokuapp.apportfoliobackend.fabriziodev.controller;

import com.herokuapp.apportfoliobackend.fabriziodev.dto.Mensaje;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@ApiIgnore
public class HomeController {

    @GetMapping("/")
    public ModelAndView homeredirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
        return null;
    }


    @GetMapping("/api/home")
    @ResponseBody
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(new Mensaje("Bienvenido a la API de FabrizioDev"));
    }

    @GetMapping("/api")
    public ModelAndView apiredirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
        return null;
    }
}
