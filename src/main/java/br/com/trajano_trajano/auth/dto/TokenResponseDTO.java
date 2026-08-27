package br.com.trajano_trajano.auth.dto;

public record TokenResponseDTO(
        String token,
        String type,
        long expirationTime
) {
}
