package br.com.trajano_trajano.post;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.post.dto.PostResponseDTO;
import br.com.trajano_trajano.tag.Tag;
import br.com.trajano_trajano.tag.TagRepository;
import br.com.trajano_trajano.user.User;
import lombok.RequiredArgsConstructor;

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

    public Post toUpdate(Post post,PostRequestDTO dto) {
        post.setTitle(dto.title());
        post.setContent(dto.content());
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
                post.getAuthor().getProfileUsername(),

                // EXTRAI APENAS OS NOMES DAS TAGS PARA O DTO
                post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                post.getCreatedAt()
        );
    }
}
