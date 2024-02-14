package com.vedruna.redsocial.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.persistence.model.Follow;
import com.vedruna.redsocial.persistence.model.User;
import com.vedruna.redsocial.persistence.repository.FollowRepositoryI;
import com.vedruna.redsocial.persistence.repository.UserRepositoryI;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz FollowServiceI que proporciona servicios relacionados con las relaciones de seguimiento en la red social.
 */
@Service
public class FollowServiceImpl implements FollowServiceI {

    private FollowRepositoryI followRepository;
    private UserRepositoryI userRepository;
    private UserServiceI userService;

    /**
     * Constructor de la clase FollowServiceImpl.
     *
     * @param followRepository Repositorio de relaciones de seguimiento.
     * @param userRepository  Repositorio de usuarios.
     * @param userService     Servicio de usuarios.
     */
    @Autowired
    public FollowServiceImpl(FollowRepositoryI followRepository, UserRepositoryI userRepository,
    		UserServiceI userService) {
    	this.followRepository = followRepository;
    	this.userRepository = userRepository;
    	this.userService = userService;
    }

    /**
     * Obtiene el usuario autenticado.
     *
     * @return DTO que representa al usuario autenticado.
     */
    private UserDTO getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }

    /**
     * Establece una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    @Override
    public void followUser(Long followerId, Long followedId) {
        UserDTO authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(followerId)) {
            throw new RuntimeException("No autorizado para seguir a un usuario en nombre de otro usuario");
        }

        List<UserDTO> followers = getFollowersByUserId(followedId);

        if (followers.stream().anyMatch(f -> f.getUserId().equals(followerId))) {
            throw new RuntimeException("Ya estás siguiendo a este usuario");
        }

        followRepository.save(new Follow(followedId, userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Seguido no encontrado")),
                userRepository.findById(followedId)
                        .orElseThrow(() -> new RuntimeException("Seguidor no encontrado"))));
    }

    /**
     * Elimina una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followedId) {
        UserDTO authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(followerId)) {
            throw new RuntimeException("No autorizado para dejar de seguir a un usuario en nombre de otro usuario");
        }

        followRepository.deleteByFollowerUserIdAndFollowedUserId(followerId, followedId);
    }

    /**
     * Obtiene una lista de usuarios seguidores de un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan a los usuarios seguidores.
     */
    @Override
    public List<UserDTO> getFollowersByUserId(Long userId) {
        List<Follow> followers = followRepository.findByFollowedUserId(userId);
        return followers.stream()
                .map(follow -> convertToDTO(follow.getFollower()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista de usuarios seguidos por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan a los usuarios seguidos.
     */
    @Override
    public List<UserDTO> getFollowingByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowing().stream()
                .map(follow -> userService.convertToDTO(follow.getFollowed()))
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad User a un objeto UserDTO.
     *
     * @param userEntity Entidad User a convertir.
     * @return Objeto UserDTO creado a partir de la entidad User.
     */
    private UserDTO convertToDTO(User userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setDescription(userEntity.getDescription());
        userDTO.setCreationDate(userEntity.getCreationDate());
        return userDTO;
    }
}
