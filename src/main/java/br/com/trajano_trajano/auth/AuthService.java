package br.com.trajano_trajano.auth;

import br.com.trajano_trajano.security.TokenProvider;
import br.com.trajano_trajano.role.Role;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.role.RoleRepository;
import br.com.trajano_trajano.user.UserRepository;
import br.com.trajano_trajano.auth.dto.LoginRequestDTO;
import br.com.trajano_trajano.auth.dto.RegisterRequestDTO;
import br.com.trajano_trajano.auth.dto.TokenResponseDTO;
import br.com.trajano_trajano.role.RoleTypeEnum;
import br.com.trajano_trajano.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    @Value("${jwt.expiration}")
    private long expirationTime;
    private final AuthMapper authMapper;

    public void register(RegisterRequestDTO dto) throws BadRequestException {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("Email ja cadastrado");
        }
        Role role = roleRepository.findByName(RoleTypeEnum.BASIC.name()).orElseGet(() -> roleRepository.save(Role.builder().name(RoleTypeEnum.BASIC.name()).build()));
        User user = authMapper.toEntity(Set.of(role), dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
            String token = tokenProvider.generateToken(authentication);
            return authMapper.toResponse(token,"Bearer",expirationTime);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credencias invalidas");
        } catch (Exception e) {
            throw new Exception("Erro interno inesperado: " + e.getMessage());
        }
    }
}