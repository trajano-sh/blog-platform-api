package br.com.trajano_trajano.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserMeResponseDTO(
        UUID id,
        String username,
        String email,
        String bio,
        Set<String> roles,
        int followersCount,
        int followingCount,
        Instant createdAt
) {
}
