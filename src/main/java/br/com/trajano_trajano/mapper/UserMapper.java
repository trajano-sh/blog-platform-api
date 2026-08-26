package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.dto.UserRequestDTO;
import br.com.trajano_trajano.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setBio(dto.bio());
        return user;
    }

    public UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getCreatedAt(),
                user.getPosts(),
                user.getComments(),
                user.getFollowers(),
                user.getFollowing()
        );
    }
}
