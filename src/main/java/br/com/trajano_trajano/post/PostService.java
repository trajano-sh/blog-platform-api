package br.com.trajano_trajano.post;

import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;

    @Transactional
    public PostResponseDTO createPost(UUID userId, PostRequestDTO dto) {
        User user = userService.findByUserIdOrThrow(userId);
        Post post = postMapper.toEntity(user, dto);
        post = postRepository.save(post);
        return postMapper.toResponse(post);
    }

    @Transactional
    public void updatePost(UUID postId,UUID userId, PostRequestDTO dto) {
        Post post = findByPostIdOrThrow(postId);
        if (!post.getAuthor().getId().equals(userId)){
            throw new ForbiddenException("Você não tem permissão para editar este post.");
        }
        postMapper.toUpdate(post,dto);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByAuthor(UUID authorId, Pageable pageable) {
        userService.findByUserIdOrThrow(authorId);
        return postRepository.findByAuthorId(authorId, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByAuthorUsername(String username, Pageable pageable) {
        userService.findByUsernameOrThrow(username);

        return postRepository.findByAuthorUsernameIgnoreCase(username, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByTag(String tagName, Pageable pageable) {
        if (tagName == null || tagName.isBlank()) {
            return getAllPosts(pageable);
        }
        String normalizedTag = tagName.trim().toLowerCase();
        return postRepository.findByTags_NameIgnoreCase(normalizedTag, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> searchPost(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return getAllPosts(pageable);
        }
        return postRepository.searchByTitleOrContent(query, pageable).map(postMapper::toResponse);
    }

    public PostResponseDTO getPostById(UUID postId) {
        Post post = findByPostIdOrThrow(postId);
        return postMapper.toResponse(post);
    }

    @Transactional
    public void likePost(UUID postId, UUID userId) {
        Post post = findByPostIdOrThrow(postId);
        User user = userService.findByUserIdOrThrow(userId);

        if (post.getLikes().contains(user)) {
            throw new BadRequestException("Você já curtiu esta publicação.");
        }
        post.getLikes().add(user);
        postRepository.save(post);
    }

    @Transactional
    public void unlikePost(UUID postId, UUID userId) {
        Post post = findByPostIdOrThrow(postId);
        User user = userService.findByUserIdOrThrow(userId);
        if (!post.getLikes().contains(user)) {
            throw new BadRequestException("Você ainda não curtiu esta publicação.");
        }
        post.getLikes().remove(user);
        postRepository.save(post);
    }

    @Transactional
    public boolean toggleLike(UUID postId, UUID userId) {
        Post post = findByPostIdOrThrow(postId);
        User user = userService.findByUserIdOrThrow(userId);

        boolean isLiked;
        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
            isLiked = false;
        } else {
            post.getLikes().add(user);
            isLiked = true;
        }

        postRepository.save(post);
        return isLiked;
    }

    public Post findByPostIdOrThrow(UUID postId) {
        log.info("Buscando post por ID: {}", postId);
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post nao encontrado"));
    }

    public void deletePost(UUID postId,UUID userId) {
        Post post = findByPostIdOrThrow(postId);
        if (!post.getAuthor().getId().equals(userId)){
            throw new ForbiddenException("Você não tem permissão para excluir este post.");
        }
        postRepository.delete(post);
    }
}
