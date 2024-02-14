package com.vedruna.redsocial.sc.security.auth.services;

import com.vedruna.redsocial.sc.security.auth.model.LoginRequest;
import com.vedruna.redsocial.sc.security.auth.model.RegisterRequest;
import com.vedruna.redsocial.sc.security.auth.model.AuthResponse;

public interface AuthServiceI {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
