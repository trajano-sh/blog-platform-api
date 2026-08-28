package br.com.trajano_trajano.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.trajano_trajano.shared.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername());
    }

    public String buildToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);
        return Jwts.builder().subject(username).issuedAt(now).expiration(expirationDate).signWith(getSigningKey()).compact();
    }

    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }
}
