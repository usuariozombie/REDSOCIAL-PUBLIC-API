package com.vedruna.redsocial.service;

import java.util.List;

import com.vedruna.redsocial.dto.UserDTO;

/**
 * Interfaz que define los servicios relacionados con las relaciones de seguimiento en la red social.
 */
public interface FollowServiceI {

    /**
     * Establece una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    void followUser(Long followerId, Long followedId);

    /**
     * Elimina una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    void unfollowUser(Long followerId, Long followedId);

    /**
     * Obtiene una lista de usuarios seguidores de un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan a los usuarios seguidores.
     */
    List<UserDTO> getFollowersByUserId(Long userId);

    /**
     * Obtiene una lista de usuarios seguidos por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan a los usuarios seguidos.
     */
    List<UserDTO> getFollowingByUserId(Long userId);

}
