package com.vedruna.redsocial.dto;

import java.time.LocalDateTime;

import com.vedruna.redsocial.persistence.model.Comment;
import com.vedruna.redsocial.persistence.model.Publication;
import com.vedruna.redsocial.persistence.model.User;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) que representa un comentario en la red social.
 */
@Getter
@Setter
public class CommentDTO {
	
    private Long userId;
    private Long publicationId;
    private String text;
    private LocalDateTime creationDate;
    
    /**
     * Convierte este objeto CommentDTO a una entidad Comment.
     *
     * @param user       Usuario que realiza el comentario.
     * @param publication Publicación a la que se realiza el comentario.
     * @return Objeto Comment creado a partir de este CommentDTO.
     */
    public Comment toEntity(User user, Publication publication) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPublication(publication);
        comment.setText(text);
        comment.setCreationDate(LocalDateTime.now());
        return comment;
    }
}
