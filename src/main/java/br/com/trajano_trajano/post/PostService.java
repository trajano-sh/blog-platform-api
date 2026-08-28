package br.com.trajano_trajano.post;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.shared.exception.BadRequestException;
import br.com.trajano_trajano.shared.exception.ForbiddenException;
import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        log.info("Post criado com sucesso: postId={}, authorId={}", post.getId(), userId);
        return postMapper.toResponse(post);
    }

    @Transactional
    public void updatePost(UUID postId, UUID userId, PostRequestDTO dto) {
        Post post = findByPostIdOrThrow(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            log.warn("Tentativa não autorizada de editar post: postId={}, userIdRequisitante={}, authorIdReal={}", 
                    postId, userId, post.getAuthor().getId());
            throw new ForbiddenException("Você não tem permissão para editar este post.");
        }

        postMapper.toUpdate(post, dto);
        postRepository.save(post);
        log.info("Post atualizado com sucesso: postId={}, authorId={}", postId, userId);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByAuthor(UUID authorId, Pageable pageable) {
        userService.findByUserIdOrThrow(authorId);
        log.debug("Listando posts por autorId: authorId={}, page={}", authorId, pageable.getPageNumber());
        return postRepository.findByAuthorId(authorId, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByAuthorUsername(String username, Pageable pageable) {
        userService.findByUsernameOrThrow(username);
        log.debug("Listando posts por username: username={}, page={}", username, pageable.getPageNumber());
        return postRepository.findByAuthorUsernameIgnoreCase(username, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        log.debug("Listando todos os posts: page={}", pageable.getPageNumber());
        return postRepository.findAll(pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByTag(String tagName, Pageable pageable) {
        if (tagName == null || tagName.isBlank()) {
            return getAllPosts(pageable);
        }
        String normalizedTag = tagName.trim().toLowerCase();
        log.debug("Listando posts por tag: tag={}, page={}", normalizedTag, pageable.getPageNumber());
        return postRepository.findByTags_NameIgnoreCase(normalizedTag, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> searchPost(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return getAllPosts(pageable);
        }
        log.debug("Buscando posts por termo: query={}, page={}", query, pageable.getPageNumber());
        return postRepository.searchByTitleOrContent(query, pageable).map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
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
        log.info("Post curtido: postId={}, userId={}", postId, userId);
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
        log.info("Curtida removida: postId={}, userId={}", postId, userId);
    }

    @Transactional
    public void deletePost(UUID postId, UUID userId) {
        Post post = findByPostIdOrThrow(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            log.warn("Tentativa não autorizada de excluir post: postId={}, userIdRequisitante={}, authorIdReal={}", 
                    postId, userId, post.getAuthor().getId());
            throw new ForbiddenException("Você não tem permissão para excluir este post.");
        }

        postRepository.delete(post);
        log.info("Post excluído com sucesso: postId={}, authorId={}", postId, userId);
    }

    public Post findByPostIdOrThrow(UUID postId) {
        log.debug("Buscando post por ID: postId={}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post não encontrado: postId={}", postId);
                    return new NotFoundException("Post não encontrado");
                });
    }
}