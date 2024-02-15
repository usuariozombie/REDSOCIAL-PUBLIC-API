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

/**
 * Controlador que maneja las solicitudes relacionadas con la autenticación.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Value("${jwt.secret}")
    private String expectedToken;

    @Autowired
    private AuthService authService;

    /**
     * Maneja la solicitud de inicio de sesión.
     *
     * @param request Datos de inicio de sesión proporcionados en el cuerpo de la solicitud.
     * @param token   Token proporcionado en el encabezado de autorización.
     * @return ResponseEntity que contiene la respuesta de autenticación.
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, @RequestHeader("Authorization") String token) {
        validateToken(token);
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Maneja la solicitud de registro de usuario.
     *
     * @param request Datos de registro proporcionados en el cuerpo de la solicitud.
     * @param token   Token proporcionado en el encabezado de autorización.
     * @return ResponseEntity que contiene la respuesta de autenticación.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, @RequestHeader("Authorization") String token) {
        validateToken(token);
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Valida si el token proporcionado coincide con el valor esperado.
     *
     * @param token Token proporcionado en el encabezado de autorización.
     */
    private void validateToken(String token) {
        if (!expectedToken.equals(token)) {
            throw new RuntimeException("Token no válido");
        }
    }
}
