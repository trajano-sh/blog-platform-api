package br.com.trajano_trajano.service;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.database.repository.UserRepository;
import br.com.trajano_trajano.dto.UserRequestDTO;
import br.com.trajano_trajano.dto.UserResponseDTO;
import br.com.trajano_trajano.exception.BusinessException;
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

    public void createUser(UserRequestDTO dto) {
        log.info("Validando informações");
        if (userRepository.existsByUsername(dto.username()) || userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Username ou email já existem");
        }
        if (dto.password().length()<6) {
            throw new BusinessException("Password precisa ter no mínimo 6 caracteres");
        }
        User user = userMapper.toEntity(dto);
        log.info("Salvando informações no banco de dados");
        userRepository.save(user);
    }

    public User findByUserIdOrThrow(UUID userId) {
        log.info("Buscando usuário por ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    public UserResponseDTO findUserById(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        return userMapper.toResponse(user);
    }

    public void deleteUser(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        log.info("Deletando usuario: {}",user.getUsername());
        userRepository.delete(user);
    }
}
