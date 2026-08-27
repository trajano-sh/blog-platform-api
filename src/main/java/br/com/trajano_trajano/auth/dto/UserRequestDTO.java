package br.com.trajano_trajano.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(@NotBlank @Size(min = 3, max = 50) String username,
                             @NotBlank @Email @Size(max = 100) String email, @NotBlank String password, String bio) {
}
