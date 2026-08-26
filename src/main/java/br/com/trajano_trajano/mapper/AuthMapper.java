package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.database.entities.Role;
import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.dto.RegisterRequestDTO;
import br.com.trajano_trajano.dto.TokenResponseDTO;
import br.com.trajano_trajano.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AuthMapper {
    public User toEntity(Set<Role> role, RegisterRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setRoles(role);
        user.setBio(dto.bio());
        return user;
    }

    public TokenResponseDTO toResponse(String token,String type,long expirationTime) {
        return new TokenResponseDTO(
                token,
                type,
                expirationTime
        );
    }
}
