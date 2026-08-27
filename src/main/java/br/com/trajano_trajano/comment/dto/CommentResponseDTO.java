package br.com.trajano_trajano.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponseDTO(
        UUID id,
        String content,
        CommentAuthorDTO author,
        Instant createdAt
) {
}
