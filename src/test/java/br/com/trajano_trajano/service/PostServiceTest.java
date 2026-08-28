package br.com.trajano_trajano.service;

import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostMapper;
import br.com.trajano_trajano.post.PostRepository;
import br.com.trajano_trajano.post.PostService;
import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserService userService;

    private PostService postService;

    private UUID authorId;
    private User author;
    private UUID postId;
    private Post post;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, postMapper, userService);

        authorId = UUID.randomUUID();
        author = new User();
        author.setId(authorId);

        postId = UUID.randomUUID();
        post = new Post();
        post.setId(postId);
        post.setAuthor(author);
        post.setLikes(new HashSet<>());
    }

    @Test
    void mustCreatePostWhenUserExists() {
        PostRequestDTO dto = mock(PostRequestDTO.class);
        PostResponseDTO responseExpected = mock(PostResponseDTO.class);

        when(userService.findByUserIdOrThrow(authorId)).thenReturn(author);
        when(postMapper.toEntity(author, dto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(responseExpected);

        PostResponseDTO result = postService.createPost(authorId, dto);

        assertThat(result).isEqualTo(responseExpected);
        verify(postRepository).save(post);
    }

    @Test
    void noMustCreatePostWhenUserDoesNotExist() {
        PostRequestDTO dto = mock(PostRequestDTO.class);
        when(userService.findByUserIdOrThrow(authorId)).thenThrow(new NotFoundException("User not found"));

        assertThatThrownBy(() -> postService.createPost(authorId, dto)).isInstanceOf(NotFoundException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    void mustUpdatePostWhenUserEhAuthor() {
        PostRequestDTO dto = mock(PostRequestDTO.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.updatePost(postId, authorId, dto);

        verify(postMapper).toUpdate(post, dto);
        verify(postRepository).save(post);
    }

    @Test
    void shouldNotUpdatePostWhenUserIsNotTheAuthor() {
        UUID otherUserId = UUID.randomUUID();
        PostRequestDTO dto = mock(PostRequestDTO.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(postId, otherUserId, dto)).isInstanceOf(ForbiddenException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    void noMustUpdatePostNonexistent() {
        PostRequestDTO dto = mock(PostRequestDTO.class);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(postId, authorId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void mustDeletePostWhenUserEhAuthor() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, authorId);

        verify(postRepository).delete(post);
    }

    @Test
    void naoMustDeletePostWhenUserIsNotAuthor() {
        UUID otherUserId = UUID.randomUUID();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(postId, otherUserId)).isInstanceOf(ForbiddenException.class);

        verify(postRepository, never()).delete(any());
    }

    @Test
    void shouldLikePostWhenNotYetLiked() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findByUserIdOrThrow(otherUser.getId())).thenReturn(otherUser);

        postService.likePost(postId, otherUser.getId());

        assertThat(post.getLikes()).contains(otherUser);
        verify(postRepository).save(post);
    }

    @Test
    void shouldNotLikeAlreadyLikedPost() {
        post.getLikes().add(author);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findByUserIdOrThrow(authorId)).thenReturn(author);

        assertThatThrownBy(() -> postService.likePost(postId, authorId)).isInstanceOf(BadRequestException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    void shouldRemoveLikeWhenAlreadyLiked() {
        post.getLikes().add(author);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findByUserIdOrThrow(authorId)).thenReturn(author);

        postService.unlikePost(postId, authorId);

        assertThat(post.getLikes()).doesNotContain(author);
        verify(postRepository).save(post);
    }

    @Test
    void shouldNotRemoveLikeWhenNotYetLiked() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findByUserIdOrThrow(authorId)).thenReturn(author);

        assertThatThrownBy(() -> postService.unlikePost(postId, authorId)).isInstanceOf(BadRequestException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    void shouldReturnPostById() {
        PostResponseDTO responseExpected = mock(PostResponseDTO.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(responseExpected);

        PostResponseDTO result = postService.getPostById(postId);

        assertThat(result).isEqualTo(responseExpected);
    }

    @Test
    void mustThrowExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(postId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void mustListAllPostsWhenTagIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> pageEmpty = new PageImpl<>(java.util.List.of(post));
        when(postRepository.findAll(pageable)).thenReturn(pageEmpty);
        when(postMapper.toResponse(post)).thenReturn(mock(PostResponseDTO.class));

        postService.getPostsByTag("   ", pageable);

        verify(postRepository).findAll(pageable);
        verify(postRepository, never()).findByTags_NameIgnoreCase(any(), any());
    }

    @Test
    void mustSearchPostsByNormalizedTag() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> pagina = new PageImpl<>(java.util.List.of(post));
        when(postRepository.findByTags_NameIgnoreCase("java", pageable)).thenReturn(pagina);
        when(postMapper.toResponse(post)).thenReturn(mock(PostResponseDTO.class));

        postService.getPostsByTag("  Java  ", pageable);

        verify(postRepository).findByTags_NameIgnoreCase("java", pageable);
    }

    @Test
    void mustListAllPostsWhenSearchQueryIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> pagina = new PageImpl<>(java.util.List.of(post));
        when(postRepository.findAll(pageable)).thenReturn(pagina);
        when(postMapper.toResponse(post)).thenReturn(mock(PostResponseDTO.class));

        postService.searchPost(null, pageable);

        verify(postRepository).findAll(pageable);
        verify(postRepository, never()).searchByTitleOrContent(any(), any());
    }
}