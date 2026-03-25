package com.arep.springserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/me")
    public String me(Authentication auth) {
        return "Usuario: " + auth.getName();
    }

    @GetMapping("/status")
    public String status() {
        return "Servidor activo";
    }
}
