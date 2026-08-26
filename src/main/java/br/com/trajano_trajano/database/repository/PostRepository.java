package br.com.trajano_trajano.database.repository;

import br.com.trajano_trajano.database.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}
