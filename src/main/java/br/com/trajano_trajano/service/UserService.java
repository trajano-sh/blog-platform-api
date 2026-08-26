package br.com.trajano_trajano.service;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.database.repository.UserRepository;
import br.com.trajano_trajano.dto.UserResponseDTO;
import br.com.trajano_trajano.exception.NotFoundException;
import br.com.trajano_trajano.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO findUserById(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        return userMapper.toResponse(user);
    }

    public void deleteUser(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        log.info("Deletando usuario: {}", user.getUsername());
        userRepository.delete(user);
    }

    public User findByUserIdOrThrow(UUID userId) {
        log.info("Buscando usuário por ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
