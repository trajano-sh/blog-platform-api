package br.com.trajano_trajano.user.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateProfileDTO(
        @Size(max = 250, message = "A bio nao pode ultrapassar 250 caracteres")
        String bio
) {
}
