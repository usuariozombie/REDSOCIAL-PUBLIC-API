package com.vedruna.redsocial.sc.security.auth.services;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.vedruna.redsocial.sc.model.SecurityUser;

import javax.crypto.SecretKey;

/**
 * Servicio que proporciona métodos para la generación y validación de tokens JWT.
 */
@Service
public class JWTService implements JWTServiceI {

    @Value("${jwt.secret}")
    String SECRET_KEY;

    /**
     * Genera un token JWT para un usuario.
     *
     * @param user Usuario para el cual se generará el token.
     * @return Token JWT generado.
     */
    @Override
    public String getToken(SecurityUser user) {
        return getToken(Map.of(
                "userId", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName()
        ), user);
    }

    /**
     * Genera un token JWT con reclamaciones adicionales para un usuario.
     *
     * @param extraClaims Reclamaciones adicionales a incluir en el token.
     * @param user        Usuario para el cual se generará el token.
     * @return Token JWT generado.
     */
    @Override
    public String getToken(Map<String, Object> extraClaims, SecurityUser user) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Expire en 24 horas
                .signWith(getKey())
                .compact();
    }

    /**
     * Obtiene la clave secreta para firmar y verificar los tokens.
     *
     * @return Clave secreta para firmar y verificar los tokens.
     */
    @Override
    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Obtiene el nombre de usuario desde un token JWT.
     *
     * @param token Token JWT.
     * @return Nombre de usuario extraído del token.
     */
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Verifica si un token JWT es válido para un usuario.
     *
     * @param token       Token JWT.
     * @param userDetails Detalles del usuario.
     * @return `true` si el token es válido, `false` de lo contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Obtiene todas las reclamaciones de un token JWT.
     *
     * @param token Token JWT.
     * @return Reclamaciones extraídas del token.
     */
    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene una reclamación específica de un token JWT.
     *
     * @param token          Token JWT.
     * @param claimsResolver Función para resolver una reclamación específica.
     * @return Reclamación extraída del token.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene la fecha de expiración de un token JWT.
     *
     * @param token Token JWT.
     * @return Fecha de expiración del token.
     */
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token Token JWT.
     * @return `true` si el token ha expirado, `false` de lo contrario.
     */
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
