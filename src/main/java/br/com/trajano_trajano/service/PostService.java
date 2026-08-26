package br.com.trajano_trajano.service;

import br.com.trajano_trajano.database.entities.Post;
import br.com.trajano_trajano.database.entities.User;
import br.com.trajano_trajano.database.repository.PostRepository;
import br.com.trajano_trajano.dto.PostRequestDTO;
import br.com.trajano_trajano.exception.NotFoundException;
import br.com.trajano_trajano.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;

    public void createPost(UUID userId, PostRequestDTO dto) {
        User user = userService.findByUserIdOrThrow(userId);
        Post post = postMapper.toEntity(user, dto);
        postRepository.save(post);
    }

    public Post findByPostIdOrThrow(UUID postId) {
        log.info("Buscando post por ID: {}", postId);
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post nao encontrado"));
    }

    public void deletePost(UUID postId) {
        Post post = findByPostIdOrThrow(postId);
        postRepository.delete(post);
    }
}
