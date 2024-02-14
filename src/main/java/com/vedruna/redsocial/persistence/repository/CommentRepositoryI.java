package com.vedruna.redsocial.persistence.repository;

import com.vedruna.redsocial.persistence.model.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz que define operaciones de acceso a datos para la entidad Comment en la base de datos.
 */
@Repository
public interface CommentRepositoryI extends JpaRepository<Comment, Long> {

    /**
     * Obtiene una lista de comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador único de la publicación.
     * @return Lista de comentarios asociados a la publicación especificada.
     */
    List<Comment> findByPublicationPublicationId(Long publicationId);
}
