package br.com.trajano_trajano.user;

import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.dto.ChangePasswordDTO;
import br.com.trajano_trajano.user.dto.UserMeResponseDTO;
import br.com.trajano_trajano.user.dto.UserProfileResponseDTO;
import br.com.trajano_trajano.user.dto.UserUpdateProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        log.debug("Perfil próprio consultado: userId={}", user.getId());
        return userMapper.userMe(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDTO userProfile(String username) {
        User targetUser = findByUsernameOrThrow(username);
        log.debug("Perfil público consultado: username={}", targetUser.getProfileUsername());
        return userMapper.userProfile(targetUser);
    }

    @Transactional
    public UserMeResponseDTO userUpdateProfile(User user, UserUpdateProfileDTO dto) {
        User managedUser = findByUserIdOrThrow(user.getId());
        userMapper.userUpdateProfile(managedUser, dto);
        userRepository.save(managedUser);

        log.info("Perfil atualizado com sucesso: userId={}", managedUser.getId());
        return userMapper.userMe(managedUser);
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordDTO dto) {
        User user = findByUserIdOrThrow(currentUser.getId());
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("A senha atual informada está incorreta.");
        }

        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new BadRequestException("A nova senha não pode ser igual à senha atual.");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        log.info("Senha alterada com sucesso: userId={}", user.getId());
    }

    @Transactional
    public void followUser(User currentUser, String usernameToFollow) {
        User follower = findByUserIdOrThrow(currentUser.getId());
        User targetUser = findByUsernameOrThrow(usernameToFollow);

        if (follower.getId().equals(targetUser.getId())) {
            throw new BadRequestException("Você não pode seguir a si mesmo");
        }
        if (follower.getFollowing().contains(targetUser)) {
            throw new BadRequestException("Você já está seguindo este usuário");
        }

        follower.getFollowing().add(targetUser);
        targetUser.getFollowers().add(follower);
        userRepository.save(follower);

        log.info("Usuário seguido: followerId={}, targetId={}", follower.getId(), targetUser.getId());
    }

    @Transactional
    public void unfollowUser(User currentUser, String usernameToUnfollow) {
        User follower = findByUserIdOrThrow(currentUser.getId());
        User targetUser = findByUsernameOrThrow(usernameToUnfollow);

        if (follower.getId().equals(targetUser.getId())) {
            throw new BadRequestException("Você não pode deixar de seguir a si mesmo");
        }
        if (!follower.getFollowing().contains(targetUser)) {
            throw new BadRequestException("Você não está seguindo este usuário.");
        }

        follower.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(follower);
        userRepository.save(follower);

        log.info("Usuário deixou de seguir: followerId={}, targetId={}", follower.getId(), targetUser.getId());
    }
    
    @Transactional
    public void deleteUser(User userAuth) {
        User user = findByUserIdOrThrow(userAuth.getId());
        userRepository.delete(user);
        log.info("Conta de usuário deletada com sucesso: userId={}", user.getId());
    }

    public UserMeResponseDTO findUserById(UUID userId) {
        User user = findByUserIdOrThrow(userId);
        return userMapper.toResponse(user);
    }

    public User findByUserIdOrThrow(UUID userId) {
        log.debug("Buscando usuário por ID: userId={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: userId={}", userId);
                    return new NotFoundException("Usuário não encontrado");
                });
    }

    public User findByUsernameOrThrow(String username) {
        log.debug("Buscando usuário por username: username={}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: username={}", username);
                    return new NotFoundException("Usuário não encontrado");
                });
    }
}
