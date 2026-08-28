package br.com.trajano_trajano.comment;

import br.com.trajano_trajano.comment.dto.CommentAuthorDTO;
import br.com.trajano_trajano.comment.dto.CommentRequestDTO;
import br.com.trajano_trajano.comment.dto.CommentResponseDTO;
import br.com.trajano_trajano.post.Post;
import br.com.trajano_trajano.user.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(Post post, User author, CommentRequestDTO dto) {
        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setAuthor(author);
        comment.setPost(post);
        return comment;
    }

    public void toUpdate(Comment comment, CommentRequestDTO dto) {
        comment.setContent(dto.content());
    }

    public CommentResponseDTO toResponse(Comment comment) {
        CommentAuthorDTO authorDTO = new CommentAuthorDTO(
                comment.getAuthor().getId(),
                comment.getAuthor().getProfileUsername()
        );
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                authorDTO,
                comment.getCreatedAt()
        );
    }
}
