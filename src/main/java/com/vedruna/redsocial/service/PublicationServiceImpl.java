package com.vedruna.redsocial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedruna.redsocial.dto.PublicationDTO;
import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.persistence.model.Publication;
import com.vedruna.redsocial.persistence.repository.PublicationRepositoryI;
import com.vedruna.redsocial.persistence.repository.UserRepositoryI;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz PublicationServiceI que proporciona servicios relacionados con las publicaciones en la red social.
 */
@Service
public class PublicationServiceImpl implements PublicationServiceI {

    private PublicationRepositoryI publicationRepository;
    private UserRepositoryI userRepository;
    private UserServiceI userService;
    private FollowServiceI followService;

    /**
     * Constructor de la clase PublicationServiceImpl.
     *
     * @param publicationRepository Repositorio de publicaciones.
     * @param userRepository        Repositorio de usuarios.
     * @param userService           Servicio de usuarios.
     * @param followService          Servicio de relaciones de seguimiento.
     */
    @Autowired
    public PublicationServiceImpl(PublicationRepositoryI publicationRepository, UserRepositoryI userRepository,
    		UserServiceI userService, FollowServiceI followService) {
    	this.publicationRepository = publicationRepository;
    	this.userRepository = userRepository;
    	this.userService = userService;
    	this.followService = followService;
    }

    /**
     * Obtiene todas las publicaciones en la red social.
     *
     * @return Lista de DTO que representan todas las publicaciones.
     */
    @Override
    public List<PublicationDTO> getAllPublications() {
        List<Publication> publications = publicationRepository.findAll();
        return publications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las publicaciones de un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan las publicaciones del usuario.
     */
    @Override
    public List<PublicationDTO> getPublicationsByUser(Long userId) {
        List<Publication> publications = publicationRepository.findByAuthorUserId(userId);
        List<PublicationDTO> publicationDTOs = publications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        System.out.println("Publications by User: " + publicationDTOs);
        return publicationDTOs;
    }

    /**
     * Obtiene las publicaciones de los usuarios seguidos por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan las publicaciones de los usuarios seguidos.
     */
    @Override
    public List<PublicationDTO> getPublicationsByUsersFollowed(Long userId) {
        List<UserDTO> following = followService.getFollowingByUserId(userId);

        if (following.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> followedIds = following.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Publication> publications = publicationRepository.findByAuthorUserIdIn(followedIds);

        return publications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Crea una nueva publicación para un usuario específico.
     *
     * @param userId         Identificador único del usuario que realiza la publicación.
     * @param publicationDTO DTO que representa la nueva publicación.
     * @return DTO que representa la publicación creada.
     */
    @Override
    public PublicationDTO createPublication(Long userId, PublicationDTO publicationDTO) {
        UserDTO authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new RuntimeException("No autorizado para crear una publicación en nombre de otro usuario");
        }

        Publication publicationEntity = new Publication();
        publicationEntity.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        publicationEntity.setText(publicationDTO.getText());
        publicationEntity.setCreationDate(LocalDateTime.now());
        publicationEntity.setEditionDate(LocalDateTime.now());

        publicationRepository.save(publicationEntity);

        return convertToDTO(publicationEntity);
    }

    /**
     * Edita una publicación existente para un usuario específico.
     *
     * @param userId          Identificador único del usuario que realiza la edición.
     * @param publicationId   Identificador único de la publicación a editar.
     * @param publicationDTO  DTO que representa la publicación editada.
     * @return DTO que representa la publicación editada.
     */
    @Override
    public PublicationDTO editPublication(Long userId, Long publicationId, PublicationDTO publicationDTO) {
        UserDTO authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new RuntimeException("No autorizado para editar una publicación en nombre de otro usuario");
        }

        Publication publicationEntity = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        publicationEntity.setText(publicationDTO.getText());
        publicationEntity.setEditionDate(LocalDateTime.now());

        publicationRepository.save(publicationEntity);

        return convertToDTO(publicationEntity);
    }

    /**
     * Elimina una publicación para un usuario específico.
     *
     * @param userId        Identificador único del usuario que realiza la eliminación.
     * @param publicationId Identificador único de la publicación a eliminar.
     */
    @Override
    public void deletePublication(Long userId, Long publicationId) {
        UserDTO authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(userId)) {
            throw new RuntimeException("No autorizado para eliminar una publicación en nombre de otro usuario");
        }

        publicationRepository.deleteById(publicationId);
    }

    /**
     * Obtiene una publicación por su identificador único.
     *
     * @param publicationId Identificador único de la publicación.
     * @return DTO que representa la publicación.
     */
    @Override
    public PublicationDTO getPublicationByPublicationId(Long publicationId) {
        Publication publicationEntity = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        return convertToDTO(publicationEntity);
    }

    /**
     * Convierte una entidad Publication a un DTO PublicationDTO.
     *
     * @param publicationEntity La entidad Publication a convertir.
     * @return DTO PublicationDTO que representa la publicación convertida.
     */
    private PublicationDTO convertToDTO(Publication publicationEntity) {
        PublicationDTO publicationDTO = new PublicationDTO();
        publicationDTO.setPublicationId(publicationEntity.getPublicationId());
        publicationDTO.setAuthorId(publicationEntity.getAuthor().getUserId());
        publicationDTO.setText(publicationEntity.getText());
        publicationDTO.setCreationDate(publicationEntity.getCreationDate());
        publicationDTO.setEditionDate(publicationEntity.getEditionDate());
        return publicationDTO;
    }
}
