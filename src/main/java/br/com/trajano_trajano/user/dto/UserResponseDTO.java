package br.com.trajano_trajano.user.dto;

import br.com.trajano_trajano.comment.Comment;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username,
        String email,
        String bio,
        Instant createdAt,
        List<Post> posts,
        List<Comment> comments,
        Set<User> followers,
        Set<User> following
) {
}
