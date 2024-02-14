package com.vedruna.redsocial.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vedruna.redsocial.persistence.model.Follow;

import jakarta.transaction.Transactional;

/**
 * Interfaz que define operaciones de acceso a datos para la entidad Follow en la base de datos.
 */
@Repository
public interface FollowRepositoryI extends JpaRepository<Follow, Long> {

    /**
     * Elimina una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    @Transactional
    @Modifying
    void deleteByFollowerUserIdAndFollowedUserId(Long followerId, Long followedId);
    
    /**
     * Obtiene una lista de relaciones de seguimiento donde el usuario es el seguido.
     *
     * @param userId Identificador único del usuario seguido.
     * @return Lista de relaciones de seguimiento donde el usuario es el seguido.
     */
    List<Follow> findByFollowedUserId(Long userId);

    /**
     * Obtiene una lista de relaciones de seguimiento donde el usuario es el seguidor.
     *
     * @param userId Identificador único del usuario seguidor.
     * @return Lista de relaciones de seguimiento donde el usuario es el seguidor.
     */
    List<Follow> findByFollowerUserId(Long userId);

    /**
     * Busca una relación de seguimiento específica entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     * @return Un objeto Optional que contiene la relación de seguimiento si se encuentra, o un Optional vacío de lo contrario.
     */
    Optional<Follow> findByFollowerUserIdAndFollowedUserId(Long followerId, Long followedId);
}
