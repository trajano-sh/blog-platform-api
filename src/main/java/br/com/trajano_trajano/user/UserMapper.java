package br.com.trajano_trajano.user;

import br.com.trajano_trajano.role.Role;
import br.com.trajano_trajano.user.dto.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserMeResponseDTO userMe(User user) {
        return new UserMeResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getFollowers().size(),
                user.getFollowing().size(),
                user.getCreatedAt()
        );
    }

    public User userUpdateProfile(User user, UserUpdateProfileDTO dto) {
        user.setBio(dto.bio());
        return user;
    }

    public UserProfileResponseDTO userProfile(User user) {
        return new UserProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getBio(),
                user.getFollowers().size(),
                user.getFollowing().size(),
                user.getPosts().size(),
                user.getCreatedAt()
        );
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
