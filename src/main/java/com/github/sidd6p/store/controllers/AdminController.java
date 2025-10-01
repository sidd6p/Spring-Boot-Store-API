package com.github.sidd6p.store.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/hello")
    public String adminEndpoint() {
        return "Admin endpoint - access restricted to ADMIN role";
    }
}
