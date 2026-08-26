package br.com.trajano_trajano.database.repository;

import br.com.trajano_trajano.database.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
