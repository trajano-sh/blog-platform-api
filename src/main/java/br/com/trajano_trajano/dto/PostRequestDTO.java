package br.com.trajano_trajano.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostRequestDTO(
        @NotBlank @Size(min = 5,max = 150) String title,
        @NotBlank @Size(min = 10,max = 20000) String content,
        @Size(max = 10) Set<@NotBlank @Size(min = 2,max = 30) @Pattern(regexp = "^[a-zA-Z0-9-]+$",message = "Tag inválida") String> tags
) {
}
