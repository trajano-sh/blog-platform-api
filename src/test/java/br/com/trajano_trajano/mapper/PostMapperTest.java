package br.com.trajano_trajano.mapper;

import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.post.PostMapper;
import br.com.trajano_trajano.post.dto.PostRequestDTO;
import br.com.trajano_trajano.tag.Tag;
import br.com.trajano_trajano.tag.TagRepository;
import br.com.trajano_trajano.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @Mock
    private TagRepository tagRepository;

    private PostMapper postMapper;

    private User author;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapper(tagRepository);

        author = new User();
        author.setId(UUID.randomUUID());
        author.setUsername("hugo");
        author.setEmail("hugo@example.com");
    }

    @Test
    void shouldNormalizeTagNamesToTinyWithoutSpaces() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Set.of("  Java  "));

        when(tagRepository.findByName("java")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        Post post = postMapper.toEntity(author, dto);

        assertThat(post.getTags()).extracting(Tag::getName).containsExactly("java");
    }

    @Test
    void shouldReuseExistingTagInsteadOfCreatingNewOne() {
        Tag existingTag = new Tag("spring");
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Set.of("Spring"));

        when(tagRepository.findByName("spring")).thenReturn(Optional.of(existingTag));

        Post post = postMapper.toEntity(author, dto);

        assertThat(post.getTags()).containsExactly(existingTag);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void shouldCreateNewTagWhenItDoesNotExist() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Set.of("kotlin"));

        when(tagRepository.findByName("kotlin")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        postMapper.toEntity(author, dto);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void ShouldNotCallTagsRepositoryWhenTagsListIsEmpty() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Set.of());

        Post post = postMapper.toEntity(author, dto);

        assertThat(post.getTags()).isEmpty();
        verify(tagRepository, never()).findByName(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void noMustCallTagsRepositoryWhenTagsENull() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", null);

        Post post = postMapper.toEntity(author, dto);

        assertThat(post.getTags()).isEmpty();
        verify(tagRepository, never()).findByName(any());
    }

    @Test
    void toResponseMustExtractOnlyTagsNames() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setTitle("Title");
        post.setContent("Content");
        post.setAuthor(author);
        post.setTags(Set.of(new Tag("java"), new Tag("spring")));

        var response = postMapper.toResponse(post);

        assertThat(response.tags()).containsExactlyInAnyOrder("java", "spring");
        assertThat(response.author()).isEqualTo("hugo");
    }
}
