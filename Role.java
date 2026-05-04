package com.example.otp.controller;

import com.example.otp.model.Role;
import com.example.otp.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    public record RegisterRequest(String login, String password, Role role) {}
    public record LoginRequest(String login, String password) {}
    public record TokenResponse(String token) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register login={} role={}", request.login(), request.role());
        authService.register(request.login(), request.password(), request.role());
        return ResponseEntity.ok().body("registered");
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        log.info("POST /api/auth/login login={}", request.login());
        return new TokenResponse(authService.login(request.login(), request.password()));
    }
}
