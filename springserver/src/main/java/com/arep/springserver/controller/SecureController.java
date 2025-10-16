package com.arep.springserver.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secure")
public class SecureController {

    @GetMapping("/me")
    public String me() {
        return "acceso autorizado";
    }
}
