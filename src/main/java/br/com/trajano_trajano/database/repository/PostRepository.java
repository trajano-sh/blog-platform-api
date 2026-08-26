package br.com.trajano_trajano.database.repository;

import br.com.trajano_trajano.database.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%',:query,'%')) "+
    "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Post> searchByTitleOrContent(@Param("query")String query, Pageable pageable);

    Page<Post> findByAuthorId(UUID authorId,Pageable pageable);

    Page<Post> findBYAuthorUsernameIgnoreCase(String username,Pageable pageable);

    Page<Post> findByTags_NameIgnoreCase(String tagName, Pageable pageable);

}
