package br.com.trajano_trajano.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.shared.pagination.PageResponse;
import br.com.trajano_trajano.user.dto.ChangePasswordDTO;
import br.com.trajano_trajano.user.dto.UserMeResponseDTO;
import br.com.trajano_trajano.user.dto.UserProfileResponseDTO;
import br.com.trajano_trajano.user.dto.UserUpdateProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponseDTO> userProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.userProfile(username));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponseDTO> userMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.userMe(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserMeResponseDTO> userUpdateProfile(@AuthenticationPrincipal User user, @RequestBody UserUpdateProfileDTO dto) {
        return ResponseEntity.ok(userService.userUpdateProfile(user, dto));
    }

    @PostMapping("/{usernameToFollow}/followers")
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal User currentUser, @PathVariable String usernameToFollow) {
        userService.followUser(currentUser, usernameToFollow);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usernameToFollow}/followers")
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal User currentUser, @PathVariable String usernameToFollow) {
        userService.unfollowUser(currentUser, usernameToFollow);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal User currentUser, @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(currentUser, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<PageResponse<PostResponseDTO>> getPostsByUsername(@PathVariable String username, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDTO> posts = postService.getPostsByAuthorUsername(username, pageable);
        return ResponseEntity.ok(PageResponse.from(posts));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User userAuth) {
        userService.deleteUser(userAuth);
        return ResponseEntity.noContent().build();
    }
}
