package br.com.trajano_trajano.database.repository;

import br.com.trajano_trajano.database.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
