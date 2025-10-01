package com.github.sidd6p.store.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin-only endpoints with restricted access")
public class AdminController {
    @GetMapping("/hello")
    @Operation(summary = "Admin hello endpoint", description = "Test endpoint for admin access verification - only accessible to users with ADMIN role")
    public String adminEndpoint() {
        return "Admin endpoint - access restricted to ADMIN role";
    }
}
