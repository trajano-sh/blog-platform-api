package br.com.trajano_trajano.comment.dto;

import java.util.UUID;

public record CommentAuthorDTO(
        UUID id,
        String username
        ) {}
