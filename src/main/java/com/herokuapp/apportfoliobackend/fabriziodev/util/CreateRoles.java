package com.herokuapp.apportfoliobackend.fabriziodev.util;

import com.herokuapp.apportfoliobackend.fabriziodev.security.entity.Rol;
import com.herokuapp.apportfoliobackend.fabriziodev.security.enums.RolNombre;
import com.herokuapp.apportfoliobackend.fabriziodev.security.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * MUY IMPORTANTE: ESTA CLASE SÓLO SE EJECUTARÁ UNA VEZ PARA CREAR LOS ROLES.
 * UNA VEZ CREADOS SE DEBERÁ ELIMINAR O BIEN COMENTAR EL CÓDIGO
 *
 */

@Component
public class CreateRoles implements CommandLineRunner {

    @Autowired
    RolService rolService;

    @Override
    public void run(String... args) throws Exception {
      /* Rol rolAdmin = new Rol(RolNombre.ROLE_ADMIN);
        Rol rolUser = new Rol(RolNombre.ROLE_USER);
        Rol rolProfesor = new Rol(RolNombre.ROLE_PROFESOR);
        rolService.save(rolAdmin);
        rolService.save(rolUser);
        rolService.save(rolProfesor);*/
    }
    
    
}
