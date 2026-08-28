package br.com.trajano_trajano.repositories;

import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostRepository;
import br.com.trajano_trajano.user.User;
import br.com.trajano_trajano.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    private User author;

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
    }

    @Test
    void shouldReturnPostsPaginatedByAuthor() {
        for (int i = 1; i <= 15; i++) {
            Post post = new Post();
            post.setTitle("Post " + i);
            post.setContent("Content " + i);
            post.setAuthor(author);
            postRepository.save(post);
        }

        Pageable firstPage = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findByAuthorId(author.getId(), firstPage);

        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void mustReturnEmptyPageWhenAuthorNoPosts() {
        UUID authorWithoutId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> result = postRepository.findByAuthorId(authorWithoutId, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void doNotSaveUntitledPost() {
        Post post = new Post();
        post.setContent("UnTitled");
        post.setAuthor(author);

        assertThatThrownBy(() -> postRepository.saveAndFlush(post))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}