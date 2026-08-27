package br.com.trajano_trajano.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(@NotBlank(message = "A senha atual é obrigatória") String currentPassword,
                                @NotBlank(message = "A nova senha é obrigatória") @Size(min = 6, max = 50, message = "A nova senha deve ter entre 6 a 50 caracteres") String newPassword,
                                @NotBlank(message = "A confirmação de senha é obrigatória") String confirmPassword
) {
}
