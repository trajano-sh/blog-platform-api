package br.com.trajano_trajano.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.key}")
    public String key;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // GERAR UM TOKEN
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername());
    }

    public String buildToken(String username) {
        Date now = new Date();

        Date expirationDate = new Date(now.getTime() + expirationTime);
        return Jwts.builder().subject(username).issuedAt(now).expiration(expirationDate).signWith(getSigningKey()).compact();
    }

    // VALIDAR TOKEN
    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // EXTRAIR INFORMACOES DO TOKEN
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

}
