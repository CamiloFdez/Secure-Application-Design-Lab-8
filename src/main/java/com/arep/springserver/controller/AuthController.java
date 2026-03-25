package com.arep.springserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arep.springserver.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        try {
            userService.register(req.username, req.password);
            return ResponseEntity.status(201).body("Usuario creado");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        if (userService.validateCredentials(req.username, req.password)) {
            return ResponseEntity.ok("Login exitoso");
        }
        return ResponseEntity.status(401).body("Credenciales invalidas");
    }

    public static class RegisterRequest {
        public String username;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
