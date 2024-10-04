package com.example.springdogless.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/*
Nota: El LoginController funciona de la mano con la carpeta config/ y el template login/
Pero hace falta introducir la dependencias adicional en el pom.xml

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>

Y en el application properties

spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always

Una vez añadidas, se configurará el logueo en el inicio, pero es recomendable
hacerlo al final del crud al 100%
Atte. Alejandro
 */
@Controller
public class LoginControllerModelo {

    @GetMapping("/loginForm")
    public String loginForm(){
        return "login/loginForm";
    }

}
