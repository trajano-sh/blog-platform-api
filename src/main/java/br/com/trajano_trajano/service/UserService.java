package br.com.trajano_trajano.service;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.database.repository.UserRepository;
import br.com.trajano_trajano.dto.UserRequestDTO;
import br.com.trajano_trajano.exception.Business;
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
        log.info("validando informacoes");
        if (userRepository.existsByUsername(dto.username()) || userRepository.existsByEmail(dto.email())) {
            throw new Business("Username ou email ja existem");
        }

        if (dto.password().length()<6) {
            throw new Business("Password precisa ter no minimo 6 caracteres");
        }

        User user = userMapper.toEntity(dto);
        log.info("Salvando informacoes no banco de dados");
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        log.info("verificando se usuario existe");
        User user = userRepository.findById(userId)
                .orElseThrow(()->new Business("Usuario nao encontrado"));
        log.info("usuario encontrado e deletando");
        userRepository.delete(user);
    }
}
