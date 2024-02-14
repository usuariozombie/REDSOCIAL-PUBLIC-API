package com.vedruna.redsocial.sc.security.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vedruna.redsocial.sc.security.auth.model.AuthResponse;
import com.vedruna.redsocial.sc.security.auth.model.LoginRequest;
import com.vedruna.redsocial.sc.security.auth.model.RegisterRequest;
import com.vedruna.redsocial.sc.security.auth.services.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Value("${jwt.secret}")
    private String expectedToken;
    
    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, @RequestHeader("Authorization") String token) {
        validateToken(token);
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, @RequestHeader("Authorization") String token) {
        validateToken(token);
        return ResponseEntity.ok(authService.register(request));
    }

    private void validateToken(String token) {
        if (!expectedToken.equals(token)) {
            throw new RuntimeException("Token no válido");
        }
    }
}
