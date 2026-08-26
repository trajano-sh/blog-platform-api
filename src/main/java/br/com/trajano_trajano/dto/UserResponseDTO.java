package br.com.trajano_trajano.dto;

import br.com.trajano_trajano.database.entities.Comment;
import br.com.trajano_trajano.database.entities.Post;
import br.com.trajano_trajano.database.entities.User;

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
