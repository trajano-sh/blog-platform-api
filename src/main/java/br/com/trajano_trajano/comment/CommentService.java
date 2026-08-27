package br.com.trajano_trajano.comment;

import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserMapper userMapper;

    @Transactional
    public void createComment(UUID postId, User user, CommentRequestDTO dto) {
        Post post = postService.findByPostIdOrThrow(postId);
        log.info("Criando comentario");
        Comment comment = commentMapper.toEntity(post, user, dto);
        commentRepository.save(comment);
        log.info("Comentario criado com sucesso");
    }

    @Transactional
    public CommentResponseDTO updatedComment(UUID commentId, User currentUser, CommentRequestDTO dto) {
        Comment comment = findCommentById(commentId);
        if (!comment.getAuthor().getId().equals(currentUser.getId()))
            throw new ForbiddenException("Você não tem permissão para editar este comentário.");
        log.info("Atualizando comentario");
        commentMapper.toUpdate(comment, dto);
        commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(UUID commentId) {
        Comment comment = findCommentById(commentId);
        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getCommentsByPost(UUID postId, Pageable pageable) {
        postService.findByPostIdOrThrow(postId);
        return commentRepository.findByPostId(postId, pageable).map(commentMapper::toResponse);
    }

    @Transactional
    public void deleteComment(UUID commentId, User currentUser) {
        Comment comment = findCommentById(commentId);
        if (!comment.getAuthor().getId().equals(currentUser.getId()))
            throw new ForbiddenException("Você não tem permissão para excluir este comentário.");
        log.info("Deletando comentario");
        commentRepository.delete(comment);
    }

    private Comment findCommentById(UUID commentId) {
        log.info("Procurando por comentario");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comentario nao encontrado"));
        return comment;
    }
}
