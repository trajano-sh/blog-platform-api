package br.com.trajano_trajano.controller;

import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.dto.PostRequestDTO;
import br.com.trajano_trajano.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@AuthenticationPrincipal User userId, @RequestBody PostRequestDTO dto) {
        postService.createPost(userId.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
