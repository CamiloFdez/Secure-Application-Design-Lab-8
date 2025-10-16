package com.arep.springserver.controller;

import com.arep.springserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterReq body) {
        users.register(body.username, body.password);
        return ResponseEntity.status(201).body("usuario creado");
    }

    public static class RegisterReq {
        public String username;
        public String password;
    }
}
