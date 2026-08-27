package br.com.trajano_trajano.post;

import br.com.trajano_trajano.comment.CommentService;
import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.shared.pagination.PageResponse;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createPost(@AuthenticationPrincipal User user, @RequestBody PostRequestDTO dto) {
        postService.createPost(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponseDTO>> getAllPosts(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDTO> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(PageResponse.from(posts));
    }

    @GetMapping("/tags/{tagName}")
    public ResponseEntity<PageResponse<PostResponseDTO>> getPostsByTag(@PathVariable String tagName, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDTO> posts = postService.getPostsByTag(tagName, pageable);
        return ResponseEntity.ok(PageResponse.from(posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<PostResponseDTO>> searchPosts(@RequestParam(name = "q", required = false) String query, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDTO> posts = postService.searchPost(query,pageable);
        return ResponseEntity.ok(PageResponse.from(posts));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> likePost(@PathVariable UUID postId, @AuthenticationPrincipal User user) {

        postService.likePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable UUID postId, @AuthenticationPrincipal User user, @RequestBody CommentRequestDTO dto){
        commentService.createComment(postId,user,dto);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponseDTO>> getCommentsByPost(@PathVariable UUID postId,Pageable pageable){
        return ResponseEntity.ok(PageResponse.from(commentService.getCommentsByPost(postId,pageable)));
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@PathVariable UUID postId, @AuthenticationPrincipal User user) {

        postService.unlikePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId,@AuthenticationPrincipal User user) {
        postService.deletePost(postId,user.getId());
        return ResponseEntity.noContent().build();
    }
}
