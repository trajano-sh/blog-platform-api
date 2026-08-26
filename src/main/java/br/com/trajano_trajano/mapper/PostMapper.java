package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.database.model.Post;
import br.com.trajano_trajano.database.model.Tag;
import br.com.trajano_trajano.database.model.User;
import br.com.trajano_trajano.database.repository.TagRepository;
import br.com.trajano_trajano.dto.PostRequestDTO;
import br.com.trajano_trajano.dto.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final TagRepository tagRepository;

    public Post toEntity(User author, PostRequestDTO dto) {
        Post post = new Post();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setAuthor(author);

        // TRATA O TEXTO, RECUPERA TAGS EXISTENTES E PERSISTE AS NOVAS
        if (dto.tags() != null && !dto.tags().isEmpty()) {
            Set<Tag> tags = dto.tags().stream().map(String::trim).map(String::toLowerCase)
                    .map(name -> tagRepository.findByName(name)
                            .orElseGet(() -> tagRepository.save(new Tag(name)))).collect(Collectors.toSet());

            post.setTags(tags);
        }
        return post;
    }

    public PostResponseDTO toResponse(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getUsername(),

                // EXTRAI APENAS OS NOMES DAS TAGS PARA O DTO
                post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                post.getCreatedAt()
        );
    }

}
