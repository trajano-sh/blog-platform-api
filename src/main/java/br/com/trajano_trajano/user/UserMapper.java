package br.com.trajano_trajano.user;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.trajano_trajano.role.Role;
import br.com.trajano_trajano.user.dto.UserMeResponseDTO;
import br.com.trajano_trajano.user.dto.UserProfileResponseDTO;
import br.com.trajano_trajano.user.dto.UserUpdateProfileDTO;

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

    public void userUpdateProfile(User user, UserUpdateProfileDTO dto) {
        user.setBio(dto.bio());
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

    public UserMeResponseDTO toResponse(User user) {
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
}
