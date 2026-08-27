package br.com.trajano_trajano.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(@NotBlank @Size(min = 3, max = 100) String content) {
}
