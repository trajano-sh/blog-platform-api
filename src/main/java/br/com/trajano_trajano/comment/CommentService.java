package br.com.trajano_trajano.comment;

import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostService postService;

    public void createComment(Post post, User user, CommentRequestDTO dto) {
        Comment comment = commentMapper.toEntity(post, user, dto);
        commentRepository.save(comment);
    }

    public Page<CommentResponseDTO> getCommentsByPost(UUID postId, Pageable pageable) {
        postService.findByPostIdOrThrow(postId);
        return commentRepository.findByPostId(postId, pageable).map(commentMapper::toResponse);
    }


    public void deleteComment(UUID commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }

    private Comment findCommentById(UUID commentId) {
        log.info("Procurando por comentario");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comentario nao encontrado"));
        return comment;
    }
}
