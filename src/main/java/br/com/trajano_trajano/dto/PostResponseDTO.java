package br.com.trajano_trajano.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PostResponseDTO(
        UUID id,
        String title,
        String content,
        String author,
        Set<String> tags,
        Instant createdAt
) {
}
