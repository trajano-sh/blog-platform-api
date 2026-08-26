package br.com.trajano_trajano.database.repository;

import br.com.trajano_trajano.database.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
