package com.vedruna.redsocial.service;

import java.util.List;
import java.util.Map;

import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.persistence.model.User;

/**
 * Interfaz que define los servicios relacionados con la gestión de usuarios en la red social.
 */
public interface UserServiceI {

    /**
     * Registra un nuevo usuario en la red social.
     *
     * @param userName     Nombre de usuario del nuevo usuario.
     * @param email        Correo electrónico del nuevo usuario.
     * @param password     Contraseña del nuevo usuario.
     * @param description  Descripción opcional del nuevo usuario.
     * @return DTO que representa el usuario registrado.
     */
    UserDTO registerUser(String userName, String email, String password, String description);

    /**
     * Inicia sesión para un usuario existente en la red social.
     *
     * @param username Nombre de usuario del usuario que inicia sesión.
     * @param password Contraseña del usuario que inicia sesión.
     * @return DTO que representa el usuario que ha iniciado sesión.
     */
    UserDTO loginUser(String username, String password);

    /**
     * Edita la descripción de un usuario existente.
     *
     * @param userId      Identificador único del usuario.
     * @param description Nueva descripción para el usuario.
     * @return DTO que representa el usuario con la descripción actualizada.
     */
    UserDTO editDescription(Long userId, String description);

    /**
     * Obtiene una lista de usuarios seguidos por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan los usuarios seguidos por el usuario especificado.
     */
    List<UserDTO> getUsersFollowedByUser(Long userId);

    /**
     * Obtiene una lista de usuarios que siguen a un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de DTO que representan los usuarios que siguen al usuario especificado.
     */
    List<UserDTO> getUsersFollowingUser(Long userId);

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario del usuario a buscar.
     * @return DTO que representa el usuario encontrado.
     */
    UserDTO getUserByUsername(String username);

    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param userId Identificador único del usuario a buscar.
     * @return DTO que representa el usuario encontrado.
     */
    UserDTO getUserByUserId(Long userId);

    /**
     * Cierra la sesión del usuario actual.
     */
    void logoutUser();

    /**
     * Obtiene el usuario autenticado actualmente en la sesión.
     *
     * @return DTO que representa el usuario autenticado.
     */
    UserDTO getAuthenticatedUser();

    /**
     * Convierte una entidad User a un DTO UserDTO.
     *
     * @param user La entidad User a convertir.
     * @return DTO UserDTO que representa el usuario convertido.
     */
    UserDTO convertToDTO(User user);

    /**
     * Edita los detalles de un usuario existente, como la descripción y el correo electrónico.
     *
     * @param userId         Identificador único del usuario.
     * @param newDescription Nueva descripción para el usuario.
     * @param newEmail       Nuevo correo electrónico para el usuario.
     * @return Mapa que contiene los resultados de la edición (éxito o mensajes de error).
     */
    Map<String, String> editUserDetails(Long userId, String newDescription, String newEmail);

    /**
     * Obtiene una lista de todos los usuarios en la red social.
     *
     * @return Lista de DTO que representan todos los usuarios.
     */
    List<UserDTO> getAllUsers();
}
