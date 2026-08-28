package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.role.Role;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserMapper;
import br.com.trajano_trajano.user.dto.UserUpdateProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserMapperTest {

    private UserMapper userMapper;
    private User user;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("hugo");
        user.setEmail("hugo@example.com");
        user.setBio("Backend dev");
        user.setCreatedAt(Instant.now());

        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Set.of(role));

        User follower = new User();
        follower.setId(UUID.randomUUID());
        User followedBy = new User();
        followedBy.setId(UUID.randomUUID());

        user.setFollowers(new HashSet<>(Set.of(follower)));
        user.setFollowing(new HashSet<>(Set.of(followedBy)));
    }

    @Test
    void TheUserMustMapAllFieldsCorrectly() {
        var result = userMapper.userMe(user);

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo("hugo");
        assertThat(result.email()).isEqualTo("hugo@example.com");
        assertThat(result.bio()).isEqualTo("Backend dev");
        assertThat(result.roles()).containsExactly("ROLE_USER");
        assertThat(result.followersCount()).isEqualTo(1);
        assertThat(result.followingCount()).isEqualTo(1);
    }

    @Test
    void UsersShouldNotDisplayTheirEmailAddressOnTheirPublicProfile() {
        var result = userMapper.userProfile(user);

        assertThat(result.username()).isEqualTo("hugo");
        assertThat(result.followersCount()).isEqualTo(1);
        assertThat(result.followingCount()).isEqualTo(1);
    }

    @Test
    void userUpdateProfileMustUpdateOnlyABio() {
        UserUpdateProfileDTO dto = new UserUpdateProfileDTO("Nova bio");

        userMapper.userUpdateProfile(user, dto);

        assertThat(user.getBio()).isEqualTo("Nova bio");
        assertThat(user.getProfileUsername()).isEqualTo("hugo");
        assertThat(user.getUsername()).isEqualTo("hugo@example.com");
    }

    @Test
    void TheResponseMustMapToTheUsername() {
        var result = userMapper.toResponse(user);

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.roles()).containsExactly("ROLE_USER");
    }
}
