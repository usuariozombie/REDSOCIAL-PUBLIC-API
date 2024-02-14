package com.vedruna.redsocial.service;

import java.util.List;

import com.vedruna.redsocial.dto.PublicationDTO;

/**
 * Interfaz que define los servicios relacionados con las publicaciones en la red social.
 */
public interface PublicationServiceI {

    /**
     * Obtiene todas las publicaciones en la red social.
     *
     * @return Lista de DTO que representan todas las publicaciones.
     */
    List<PublicationDTO> getAllPublications();

    /**
     * Obtiene las publicaciones de un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan las publicaciones del usuario.
     */
    List<PublicationDTO> getPublicationsByUser(Long userId);

    /**
     * Obtiene las publicaciones de los usuarios seguidos por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan las publicaciones de los usuarios seguidos.
     */
    List<PublicationDTO> getPublicationsByUsersFollowed(Long userId);

    /**
     * Crea una nueva publicación para un usuario específico.
     *
     * @param userId          Identificador único del usuario que realiza la publicación.
     * @param publicationDTO  DTO que representa la nueva publicación.
     * @return DTO que representa la publicación creada.
     */
    PublicationDTO createPublication(Long userId, PublicationDTO publicationDTO);

    /**
     * Edita una publicación existente para un usuario específico.
     *
     * @param userId          Identificador único del usuario que realiza la edición.
     * @param publicationId   Identificador único de la publicación a editar.
     * @param publicationDTO  DTO que representa la publicación editada.
     * @return DTO que representa la publicación editada.
     */
    PublicationDTO editPublication(Long userId, Long publicationId, PublicationDTO publicationDTO);

    /**
     * Elimina una publicación para un usuario específico.
     *
     * @param userId        Identificador único del usuario que realiza la eliminación.
     * @param publicationId Identificador único de la publicación a eliminar.
     */
    void deletePublication(Long userId, Long publicationId);

    /**
     * Obtiene una publicación por su identificador único.
     *
     * @param publicationId Identificador único de la publicación.
     * @return DTO que representa la publicación.
     */
	PublicationDTO getPublicationByPublicationId(Long publicationId);
}
