package br.com.trajano_trajano.repositories;

import br.com.trajano_trajano.role.Role;
import br.com.trajano_trajano.role.RoleRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
    @Autowired
    private RoleRepository roleRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Test
    void shouldFindRoleByName() {
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);

        Optional<Role> result = roleRepository.findByName("ROLE_USER");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldReturnEmptyWhenRoleDoesNotExist() {
        Optional<Role> result = roleRepository.findByName("ROLE_ABSENT");

        assertThat(result).isEmpty();
    }

    @Test
    void ShouldNotAllowTwoRolesWithSameName() {
        Role role1 = new Role();
        role1.setName("ROLE_ADMIN");
        roleRepository.saveAndFlush(role1);

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        assertThatThrownBy(() -> roleRepository.saveAndFlush(role2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
