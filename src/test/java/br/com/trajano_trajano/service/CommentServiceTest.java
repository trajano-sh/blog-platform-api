package br.com.trajano_trajano.service;

import br.com.trajano_trajano.comment.Comment;
import br.com.trajano_trajano.comment.CommentMapper;
import br.com.trajano_trajano.comment.CommentRepository;
import br.com.trajano_trajano.comment.CommentService;
import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    private CommentService commentService;

    private UUID postId;
    private Post post;
    private User author;
    private UUID commentId;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentMapper, commentRepository, postService);

        postId = UUID.randomUUID();
        post = new Post();
        post.setId(postId);

        author = new User();
        author.setId(UUID.randomUUID());

        commentId = UUID.randomUUID();
        comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(author);
        comment.setPost(post);
    }

    @Test
    void shouldCreateCommentWhenPostExists() {
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        CommentResponseDTO responseExpected = mock(CommentResponseDTO.class);

        when(postService.findByPostIdOrThrow(postId)).thenReturn(post);
        when(commentMapper.toEntity(post, author, dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(responseExpected);

        CommentResponseDTO result = commentService.createComment(postId, author, dto);

        assertThat(result).isEqualTo(responseExpected);
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldNotCreateCommentWhenPostDoesNotExist() {
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        when(postService.findByPostIdOrThrow(postId)).thenThrow(new NotFoundException("Post not found"));

        assertThatThrownBy(() -> commentService.createComment(postId, author, dto))
                .isInstanceOf(NotFoundException.class);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void mustUpdateCommentWhenUserEhAuthor() {
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        CommentResponseDTO responseExpected = mock(CommentResponseDTO.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(responseExpected);

        CommentResponseDTO result = commentService.updateComment(commentId, author, dto);

        assertThat(result).isEqualTo(responseExpected);
        verify(commentMapper).toUpdate(comment, dto);
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldNotUpdateCommentWhenUserIsNotAuthor() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        CommentRequestDTO dto = mock(CommentRequestDTO.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(commentId, otherUser, dto))
                .isInstanceOf(ForbiddenException.class);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void noMustUpdateCommentNonexistent() {
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment(commentId, author, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void mustDeleteCommentWhenUserEhAuthor() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId, author);

        verify(commentRepository).delete(comment);
    }

    @Test
    void naoMustDeleteCommentWhenUserNaoEhAuthor() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(commentId, otherUser))
                .isInstanceOf(ForbiddenException.class);

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void shouldReturnCommentById() {
        CommentResponseDTO responseExpected = mock(CommentResponseDTO.class);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(responseExpected);

        CommentResponseDTO result = commentService.getCommentById(commentId);

        assertThat(result).isEqualTo(responseExpected);
    }

    @Test
    void mustLaunchExceptionWhenCommentDoesNotExist() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(commentId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void mustListCommentsByPostWhenPostExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> pagina = new PageImpl<>(java.util.List.of(comment));
        CommentResponseDTO responseExpected = mock(CommentResponseDTO.class);

        when(postService.findByPostIdOrThrow(postId)).thenReturn(post);
        when(commentRepository.findByPostId(postId, pageable)).thenReturn(pagina);
        when(commentMapper.toResponse(comment)).thenReturn(responseExpected);

        Page<CommentResponseDTO> result = commentService.getCommentsByPost(postId, pageable);

        assertThat(result.getContent()).containsExactly(responseExpected);
    }

    @Test
    void noMustListCommentsWhenPostDoesNotExist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postService.findByPostIdOrThrow(postId)).thenThrow(new NotFoundException("Post not found"));

        assertThatThrownBy(() -> commentService.getCommentsByPost(postId, pageable))
                .isInstanceOf(NotFoundException.class);

        verify(commentRepository, never()).findByPostId(any(), any());
    }
}
