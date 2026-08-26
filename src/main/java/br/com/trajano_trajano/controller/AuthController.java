package br.com.trajano_trajano.controller;

import br.com.trajano_trajano.dto.LoginRequestDTO;
import br.com.trajano_trajano.dto.RegisterRequestDTO;
import br.com.trajano_trajano.dto.TokenResponseDTO;
import br.com.trajano_trajano.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO dto) throws BadRequestException {
        authService.register(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dto) throws Exception {
        return ResponseEntity.ok(authService.login(dto));
    }
}
