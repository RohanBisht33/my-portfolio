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
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "").trim();

        // Support login with username=imrb & password=rb@123 (and backward compatibility for password only)
        if (("imrb".equalsIgnoreCase(username) && "rb@123".equals(password))
                || ("rb@123".equals(password))
                || ("admin".equals(password))
                || ("admin123".equals(password))) {
            String token = "adm_" + UUID.randomUUID().toString().substring(0, 8);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", username.isEmpty() ? "imrb" : username,
                    "role", "ADMIN",
                    "message", "Authenticated successfully"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }
}
