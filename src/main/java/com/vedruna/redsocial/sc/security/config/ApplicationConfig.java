package com.vedruna.redsocial.sc.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vedruna.redsocial.sc.model.UserRepository;

/**
 * Configuración de la aplicación relacionada con la autenticación.
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    private UserRepository userRepository;

    /**
     * Configuración del administrador de autenticación.
     *
     * @param config Configuración de autenticación proporcionada por Spring.
     * @return El administrador de autenticación configurado.
     * @throws Exception Si ocurre un error durante la configuración del administrador de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Proveedor de autenticación que utiliza un servicio de detalles de usuario y un codificador de contraseñas.
     *
     * @return El proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Servicio de detalles de usuario que busca usuarios por nombre de usuario en el repositorio de usuarios.
     *
     * @return El servicio de detalles de usuario configurado.
     */
    @Bean
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Codificador de contraseñas que utiliza el algoritmo BCrypt.
     *
     * @return El codificador de contraseñas configurado.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
