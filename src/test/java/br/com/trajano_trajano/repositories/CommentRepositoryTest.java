package br.com.trajano_trajano.repositories;

import br.com.trajano_trajano.comment.Comment;
import br.com.trajano_trajano.comment.CommentRepository;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostRepository;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    private User author;
    private Post post;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @BeforeEach
    void setUp() {
        author = new User();
        author.setUsername("hugo");
        author.setEmail("hugo@example.com");
        author.setPassword("hash-fake");
        userRepository.save(author);

        post = new Post();
        post.setTitle("Post of test");
        post.setContent("Content");
        post.setAuthor(author);
        postRepository.save(post);
    }

    @Test
    void shouldReturnPaginatedCommentsByPost() {
        for (int i = 1; i <= 12; i++) {
            Comment comment = new Comment();
            comment.setContent("Comment " + i);
            comment.setAuthor(author);
            comment.setPost(post);
            commentRepository.save(comment);
        }

        Pageable firstPage = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByPostId(post.getId(), firstPage);

        assertThat(result.getTotalElements()).isEqualTo(12);
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyPageWhenPostHasNoComments() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Comment> result = commentRepository.findByPostId(post.getId(), pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void noHeMustReturnCommentsOtherPost() {
        Post outroPost = new Post();
        outroPost.setTitle("Other post");
        outroPost.setContent("Other content");
        outroPost.setAuthor(author);
        postRepository.save(outroPost);

        Comment commentFromTheOtherPost = new Comment();
        commentFromTheOtherPost.setContent("It shouldn't appear");
        commentFromTheOtherPost.setAuthor(author);
        commentFromTheOtherPost.setPost(outroPost);
        commentRepository.save(commentFromTheOtherPost);

        Page<Comment> result = commentRepository.findByPostId(post.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void mustDeleteCommentsWhenDeletingPost() {
        Comment comment = new Comment();
        comment.setContent("It's going to disappear along with the post.");
        comment.setAuthor(author);
        comment.setPost(post);
        post.getComments().add(comment);
        postRepository.saveAndFlush(post);

        postRepository.delete(post);
        postRepository.flush();

        Page<Comment> result = commentRepository.findByPostId(post.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }
}
