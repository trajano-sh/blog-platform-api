package br.com.trajano_trajano.user;

import br.com.trajano_trajano.auth.dto.UserRequestDTO;
import br.com.trajano_trajano.user.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto){
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
