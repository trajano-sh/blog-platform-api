package br.com.trajano_trajano.user;

import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserMeResponseDTO userMe(User currentUser) {
        User user = findByUserIdOrThrow(currentUser.getId());
        return userMapper.userMe(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDTO userProfile(String username) {
        User targetUser = findByUsernameOrThrow(username);
        return userMapper.userProfile(targetUser);
    }

    @Transactional
    public UserMeResponseDTO userUpdateProfile(User user, UserUpdateProfileDTO dto) {
        User menagedUser = findByUserIdOrThrow(user.getId());

        userMapper.userUpdateProfile(menagedUser, dto);
        userRepository.save(menagedUser);
        return userMapper.userMe(menagedUser);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword()))
            throw new BadRequestException("A senha atual está incorreta");
        if (!dto.newPassword().equals(dto.confirmPassword()))
            throw new BadRequestException("A nova senha e a confirmação não coincidem");
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword()))
            throw new BadRequestException("A nova senha não pode ser igual à senha atual");
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void followUser(User currentUser, String usernameToFollow) {
        User follower = findByUserIdOrThrow(currentUser.getId());
        User targetUser = findByUsernameOrThrow(usernameToFollow);
        if (follower.getId().equals(targetUser.getId()))
            throw new BadRequestException("Voce não pode seguir a si mesmo");
        if (follower.getFollowing().contains(targetUser))
            throw new BadRequestException("Você já está seguindo este usuário");
        follower.getFollowing().add(targetUser);
        targetUser.getFollowers().add(follower);
        userRepository.save(follower);
    }

    @Transactional
    public void unfollowUser(User currentUser, String usernameToUnfollow) {
        User follower = findByUserIdOrThrow(currentUser.getId());
        User targetUser = findByUsernameOrThrow(usernameToUnfollow);

        if (follower.getId().equals(targetUser.getId()))
            throw new BadRequestException("Você não pode deixar de seguir a si mesmo");
        if (!follower.getFollowing().contains(targetUser))
            throw new BadRequestException("Você não está seguindo este usuário.");
        follower.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(follower);
        userRepository.save(follower);
    }

    @Transactional
    public boolean toggleFollow(User currentUser, String usernameToToggle) {
        User follower = findByUserIdOrThrow(currentUser.getId());
        User targetUser = findByUsernameOrThrow(usernameToToggle);

        if (follower.getId().equals(targetUser.getId()))
            throw new BadRequestException("Você não pode seguir a si mesmo.");

        boolean isFollowing;
        if (follower.getFollowing().contains(targetUser)) {
            follower.getFollowing().remove(targetUser);
            targetUser.getFollowers().remove(follower);
            isFollowing = false;
        } else {
            follower.getFollowing().add(targetUser);
            targetUser.getFollowers().add(follower);
            isFollowing = true;
        }
        userRepository.save(follower);
        return isFollowing;
    }

    @Transactional
    public void deleteUser(User userAuth) {
        log.info("Verificando Usuário.");
        User user = findByUserIdOrThrow(userAuth.getId());
        log.info("Deletando Usuário: {}", user.getUsername());
        userRepository.delete(user);
    }

    public UserResponseDTO findUserById(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        return userMapper.toResponse(user);
    }

    public User findByUserIdOrThrow(UUID userId) {
        log.info("Buscando usuário por ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public User findByUsernameOrThrow(String username) {
        log.info("Procurando usuário por username: {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
