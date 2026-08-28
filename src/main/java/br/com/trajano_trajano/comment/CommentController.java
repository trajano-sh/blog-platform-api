package br.com.trajano_trajano.comment;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @DeleteMapping("/{comments}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal User user, @PathVariable UUID commentId) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable UUID commentId, @AuthenticationPrincipal User currentUser,@RequestBody CommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, currentUser, dto));
    }
}
