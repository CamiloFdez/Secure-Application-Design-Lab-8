package com.arep.springserver.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arep.springserver.service.UserService;
import com.arep.springserver.service.UserService.UserSummary;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    private final UserService userService;

    public SecureController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello(Authentication auth) {
        return "Hola, " + auth.getName();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserSummary> profile(Authentication auth) {
        return userService.getProfile(auth.getName())
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/users")
    public List<UserSummary> users() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummary> userById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/status")
    public String status(Authentication auth) {
        return "Servidor activo para: " + auth.getName();
    }
}
