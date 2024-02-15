package com.vedruna.redsocial.sc.security.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vedruna.redsocial.sc.model.Role;
import com.vedruna.redsocial.sc.model.SecurityUser;
import com.vedruna.redsocial.sc.model.UserRepository;
import com.vedruna.redsocial.sc.security.auth.model.LoginRequest;
import com.vedruna.redsocial.sc.security.auth.model.RegisterRequest;
import com.vedruna.redsocial.sc.security.auth.model.AuthResponse;

/**
 * Servicio de autenticación que proporciona métodos para iniciar sesión y registrar usuarios.
 */
@Service
public class AuthService implements AuthServiceI {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTServiceI jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Realiza el proceso de inicio de sesión y genera un token JWT.
     *
     * @param request Objeto de solicitud de inicio de sesión que contiene el nombre de usuario y la contraseña.
     * @return Objeto AuthResponse que contiene el token JWT generado.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return new AuthResponse(jwtService.getToken(user));
    }

    /**
     * Registra un nuevo usuario y genera un token JWT.
     *
     * @param request Objeto de solicitud de registro que contiene la información del nuevo usuario.
     * @return Objeto AuthResponse que contiene el token JWT generado.
     */
    public AuthResponse register(RegisterRequest request) {
        SecurityUser user = new SecurityUser(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getCountry(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );
        userRepository.save(user);
        return new AuthResponse(jwtService.getToken(user));
    }
}
