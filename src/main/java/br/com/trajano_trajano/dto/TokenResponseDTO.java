package br.com.trajano_trajano.dto;

public record TokenResponseDTO(
        String token,
        String type,
        long expirationTime
) {
}
