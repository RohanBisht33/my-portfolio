package com.portfolio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ADMIN_PASSWORD = "admin";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password != null && (password.equals(ADMIN_PASSWORD) || password.equals("admin123") || password.equals("rohan"))) {
            String token = "adm_" + UUID.randomUUID().toString().substring(0, 8);
            return ResponseEntity.ok(Map.of("token", token, "message", "Authenticated successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
    }
}
