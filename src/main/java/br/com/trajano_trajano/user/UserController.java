package br.com.trajano_trajano.user;

import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.user.dto.UserResponseDTO;
import br.com.trajano_trajano.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByUsername(
            @PathVariable String username,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(postService.getPostsByAuthorUsername(username, pageable));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
