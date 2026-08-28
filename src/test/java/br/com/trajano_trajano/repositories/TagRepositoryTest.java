package br.com.trajano_trajano.repositories;


import br.com.trajano_trajano.tag.Tag;
import br.com.trajano_trajano.tag.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private TagRepository tagRepository;

    @Test
    void shouldFindTagByName() {
        Tag tag = new Tag("java");
        tagRepository.save(tag);

        Optional<Tag> result = tagRepository.findByName("java");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("java");
    }

    @Test
    void shouldReturnEmptyWhenTagDoesNotExist() {
        Optional<Tag> result = tagRepository.findByName("absent");

        assertThat(result).isEmpty();
    }

    @Test
    void ShouldNotAllowTwoTagsWithSameName() {
        tagRepository.saveAndFlush(new Tag("spring"));

        assertThatThrownBy(() -> tagRepository.saveAndFlush(new Tag("spring")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
