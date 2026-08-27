package br.com.trajano_trajano.shared.exception;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp
) {
}
