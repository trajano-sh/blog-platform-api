package br.com.trajano_trajano.comment;

import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @DeleteMapping("/{comments}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal User user, @PathVariable UUID comments){
        commentService.deleteComment(comments,user);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{comments}")
    public ResponseEntity<Void> updateComment(@AuthenticationPrincipal User user, @PathVariable UUID comments, @RequestBody CommentRequestDTO dto){
        commentService.updatedComment(comments,user,dto);
        return ResponseEntity.noContent().build();
    }
}
