package com.vedruna.redsocial.service;

import com.vedruna.redsocial.dto.CommentDTO;

import java.util.List;

/**
 * Interfaz que define los servicios relacionados con los comentarios en la red social.
 */
public interface CommentServiceI {

    /**
     * Agrega un comentario a una publicación.
     *
     * @param userId       Identificador único del usuario que realiza el comentario.
     * @param publicationId Identificador único de la publicación a la que se realiza el comentario.
     * @param commentDTO    DTO que representa el comentario a agregar.
     * @return DTO que representa el comentario agregado.
     */
    CommentDTO addComment(Long userId, Long publicationId, CommentDTO commentDTO);

    /**
     * Obtiene una lista de comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador único de la publicación.
     * @return Lista de DTO que representan los comentarios asociados a la publicación especificada.
     */
    List<CommentDTO> getCommentsByPublicationId(Long publicationId);
}
