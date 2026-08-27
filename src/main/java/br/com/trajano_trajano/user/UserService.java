package br.com.trajano_trajano.user;

import br.com.trajano_trajano.user.dto.UserResponseDTO;
import br.com.trajano_trajano.shared.exception.NotFoundException;
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

    public User findByUserIdOrThrow(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new NotFoundException("Usuário não encontrado"));
    }
}
