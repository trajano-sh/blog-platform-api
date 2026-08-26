package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
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
