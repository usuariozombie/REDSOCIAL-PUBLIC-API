package com.vedruna.redsocial.service;

import com.vedruna.redsocial.dto.CommentDTO;
import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.persistence.model.Comment;
import com.vedruna.redsocial.persistence.repository.CommentRepositoryI;
import com.vedruna.redsocial.persistence.repository.PublicationRepositoryI;
import com.vedruna.redsocial.persistence.repository.UserRepositoryI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz CommentServiceI que proporciona servicios relacionados con los comentarios en la red social.
 */
@Service
public class CommentServiceImpl implements CommentServiceI {

    private final CommentRepositoryI commentRepository;
    private final UserRepositoryI userRepository;
    private final PublicationRepositoryI publicationRepository;
    private UserServiceI userService;

    /**
     * Constructor de la clase CommentServiceImpl.
     *
     * @param commentRepository     Repositorio de comentarios.
     * @param userRepository        Repositorio de usuarios.
     * @param publicationRepository Repositorio de publicaciones.
     * @param userService           Servicio de usuarios.
     */
    @Autowired
    public CommentServiceImpl(
        CommentRepositoryI commentRepository,
        UserRepositoryI userRepository,
        PublicationRepositoryI publicationRepository,
        UserServiceI userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.userService = userService;
    }

    /**
     * Obtiene una lista de comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador único de la publicación.
     * @return Lista de DTO que representan los comentarios asociados a la publicación especificada.
     */
    @Override
    public List<CommentDTO> getCommentsByPublicationId(Long publicationId) {
        List<Comment> comments = commentRepository.findByPublicationPublicationId(publicationId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return commentDTOs;
    }

    /**
     * Agrega un comentario a una publicación.
     *
     * @param userId       Identificador único del usuario que realiza el comentario.
     * @param publicationId Identificador único de la publicación a la que se realiza el comentario.
     * @param commentDTO    DTO que representa el comentario a agregar.
     * @return DTO que representa el comentario agregado.
     */
    @Override
    @Transactional
    public CommentDTO addComment(Long userId, Long publicationId, CommentDTO commentDTO) {
        UserDTO authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new RuntimeException("No autorizado para agregar un comentario en nombre de otro usuario");
        }

        Comment commentEntity = new Comment();
        commentEntity.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        commentEntity.setPublication(publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada")));
        commentEntity.setText(commentDTO.getText());
        
        commentEntity.setCreationDate(LocalDateTime.now());

        commentRepository.save(commentEntity);

        return convertToDTO(commentEntity);
    }

    /**
     * Convierte una entidad Comment a un objeto CommentDTO.
     *
     * @param commentEntity Entidad Comment a convertir.
     * @return Objeto CommentDTO creado a partir de la entidad Comment.
     */
    private CommentDTO convertToDTO(Comment commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUserId(commentEntity.getUser().getUserId());
        commentDTO.setPublicationId(commentEntity.getPublication().getPublicationId());
        commentDTO.setText(commentEntity.getText());

        if (commentEntity.getCreationDate() != null) {
            commentDTO.setCreationDate(commentEntity.getCreationDate());
        }

        return commentDTO;
    }

}
