package br.com.trajano_trajano.comment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public CommentResponseDTO createComment(UUID postId, User user, CommentRequestDTO dto) {
        Post post = postService.findByPostIdOrThrow(postId);
        Comment comment = commentMapper.toEntity(post, user, dto);
        comment = commentRepository.save(comment);

        log.info("Comentário criado com sucesso: commentId={}, postId={}, authorId={}", 
                comment.getId(), postId, user.getId());
        return commentMapper.toResponse(comment);
    }

    @Transactional
    public CommentResponseDTO updatedComment(UUID commentId, User currentUser, CommentRequestDTO dto) {
        Comment comment = findCommentById(commentId);

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            log.warn("Tentativa não autorizada de editar comentário: commentId={}, requesterId={}, authorId={}", 
                    commentId, currentUser.getId(), comment.getAuthor().getId());
            throw new ForbiddenException("Você não tem permissão para editar este comentário.");
        }

        commentMapper.toUpdate(comment, dto);
        commentRepository.save(comment);

        log.info("Comentário atualizado com sucesso: commentId={}, authorId={}", commentId, currentUser.getId());
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
        log.debug("Listando comentários por post: postId={}, page={}", postId, pageable.getPageNumber());
        return commentRepository.findByPostId(postId, pageable).map(commentMapper::toResponse);
    }

    @Transactional
    public void deleteComment(UUID commentId, User currentUser) {
        Comment comment = findCommentById(commentId);

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            log.warn("Tentativa não autorizada de excluir comentário: commentId={}, requesterId={}, authorId={}", 
                    commentId, currentUser.getId(), comment.getAuthor().getId());
            throw new ForbiddenException("Você não tem permissão para excluir este comentário.");
        }

        commentRepository.delete(comment);
        log.info("Comentário excluído com sucesso: commentId={}, authorId={}", commentId, currentUser.getId());
    }

    private Comment findCommentById(UUID commentId) {
        log.debug("Buscando comentário por ID: commentId={}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comentário não encontrado: commentId={}", commentId);
                    return new NotFoundException("Comentário não encontrado");
                });
    }
}