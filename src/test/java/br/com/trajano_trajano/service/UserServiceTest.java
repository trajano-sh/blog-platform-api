package br.com.trajano_trajano.service;

import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserMapper;
import br.com.trajano_trajano.user.UserRepository;
import br.com.trajano_trajano.user.UserService;
import br.com.trajano_trajano.user.dto.ChangePasswordDTO;
import br.com.trajano_trajano.user.dto.UserMeResponseDTO;
import br.com.trajano_trajano.user.dto.UserProfileResponseDTO;
import br.com.trajano_trajano.user.dto.UserUpdateProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, passwordEncoder);

        currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setUsername("hugo");
        currentUser.setFollowing(new HashSet<>());
        currentUser.setFollowers(new HashSet<>());

        targetUser = new User();
        targetUser.setId(UUID.randomUUID());
        targetUser.setUsername("other");
        targetUser.setFollowing(new HashSet<>());
        targetUser.setFollowers(new HashSet<>());
    }

    @Test
    void shouldReturnOwnProfile() {
        UserMeResponseDTO responseExpected = mock(UserMeResponseDTO.class);
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userMapper.userMe(currentUser)).thenReturn(responseExpected);

        UserMeResponseDTO result = userService.userMe(currentUser);

        assertThat(result).isEqualTo(responseExpected);
    }

    @Test
    void mustReturnPublicProfileByUsername() {
        UserProfileResponseDTO responseExpected = mock(UserProfileResponseDTO.class);
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(targetUser));
        when(userMapper.userProfile(targetUser)).thenReturn(responseExpected);

        UserProfileResponseDTO result = userService.userProfile("other");

        assertThat(result).isEqualTo(responseExpected);
    }

    @Test
    void mustThrowExceptionWhenUsernameDoesNotExist() {
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.userProfile("absent")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateProfile() {
        UserUpdateProfileDTO dto = mock(UserUpdateProfileDTO.class);
        UserMeResponseDTO responseExpected = mock(UserMeResponseDTO.class);

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userMapper.userMe(currentUser)).thenReturn(responseExpected);

        UserMeResponseDTO result = userService.userUpdateProfile(currentUser, dto);

        assertThat(result).isEqualTo(responseExpected);
        verify(userMapper).userUpdateProfile(currentUser, dto);
        verify(userRepository).save(currentUser);
    }

    @Test
    void mustChangePasswordWhenCurrentPassCorrectAndNewIsDifferent() {
        currentUser.setPassword("currentHash");
        ChangePasswordDTO dto = new ChangePasswordDTO("currentPass", "newPass", "newPass");

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("currentPass", "currentHash")).thenReturn(true);
        when(passwordEncoder.matches("newPass", "currentHash")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("hashNova");

        userService.changePassword(currentUser, dto);

        assertThat(currentUser.getPassword()).isEqualTo("hashNova");
        verify(userRepository).save(currentUser);
    }

    @Test
    void shouldNotChangePasswordWhenCurrentPasswordIsIncorrect() {
        currentUser.setPassword("currentHash");
        ChangePasswordDTO dto = new ChangePasswordDTO("wrongPass", "newPass", "newPass");

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("wrongPass", "currentHash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(currentUser, dto)).isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void DontChangePasswordWhenNewPasswordEqualCurrent() {
        currentUser.setPassword("currentHash");
        ChangePasswordDTO dto = new ChangePasswordDTO("currentPass", "currentPass", "newPass");

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("currentPass", "currentHash")).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword(currentUser, dto)).isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFollowOtherUser() {
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(targetUser));

        userService.followUser(currentUser, "other");

        assertThat(currentUser.getFollowing()).contains(targetUser);
        assertThat(targetUser.getFollowers()).contains(currentUser);
        verify(userRepository).save(currentUser);
    }

    @Test
    void mustNotFollowItself() {
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("hugo")).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> userService.followUser(currentUser, "hugo")).isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotFollowUserAlreadyFollowed() {
        currentUser.getFollowing().add(targetUser);

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(targetUser));

        assertThatThrownBy(() -> userService.followUser(currentUser, "other")).isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldStopFollowingUser() {
        currentUser.getFollowing().add(targetUser);
        targetUser.getFollowers().add(currentUser);

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(targetUser));

        userService.unfollowUser(currentUser, "other");

        assertThat(currentUser.getFollowing()).doesNotContain(targetUser);
        assertThat(targetUser.getFollowers()).doesNotContain(currentUser);
        verify(userRepository).save(currentUser);
    }

    @Test
    void youShouldNotStopFollowingYourSelf() {
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("hugo")).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> userService.unfollowUser(currentUser, "hugo")).isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void YouShouldNotUnfollowThoseWhoDontFollowYouBack() {
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(targetUser));

        assertThatThrownBy(() -> userService.unfollowUser(currentUser, "other")).isInstanceOf(BadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        userService.deleteUser(currentUser);

        verify(userRepository).delete(currentUser);
    }

    @Test
    void mustThrowExceptionWhenIdDoesNotExist() {
        UUID idAbsent = UUID.randomUUID();
        when(userRepository.findById(idAbsent)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUserIdOrThrow(idAbsent)).isInstanceOf(NotFoundException.class);
    }
}
