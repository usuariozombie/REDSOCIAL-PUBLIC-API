package com.vedruna.redsocial.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vedruna.redsocial.persistence.model.Publication;

/**
 * Interfaz que define operaciones de acceso a datos para la entidad Publication en la base de datos.
 */
@Repository
public interface PublicationRepositoryI extends JpaRepository<Publication, Long> {

    /**
     * Obtiene una lista de publicaciones realizadas por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de publicaciones realizadas por el usuario especificado.
     */
    List<Publication> findByAuthorUserId(Long userId);

    /**
     * Obtiene una lista de publicaciones realizadas por los seguidores de un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de publicaciones realizadas por los seguidores del usuario especificado.
     */
    @Query(value = "SELECT p.* FROM RS_PUBLICATION p " +
            "JOIN RS_USER u ON p.RS_PUBLICATION_AUTHOR = u.RS_USER_ID " +
            "JOIN RS_FOLLOW f ON u.RS_USER_ID = f.RS_FOLLOW_FOLLOWER " +
            "WHERE f.RS_FOLLOW_FOLLOWED = :userId", nativeQuery = true)
    List<Publication> findByAuthorFollowersUserId(Long userId);
	
    /**
     * Obtiene una lista de publicaciones realizadas por varios usuarios.
     *
     * @param authorUserIds Lista de identificadores únicos de usuarios.
     * @return Lista de publicaciones realizadas por los usuarios especificados.
     */
    List<Publication> findByAuthorUserIdIn(List<Long> authorUserIds);
}
