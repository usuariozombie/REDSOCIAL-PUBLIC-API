package com.vedruna.redsocial.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.persistence.model.User;
import com.vedruna.redsocial.persistence.repository.UserRepositoryI;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de la interfaz UserServiceI que proporciona servicios relacionados con la gestión de usuarios.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserServiceI {

    private UserRepositoryI userRepository;
    
    private PasswordEncoder passwordEncoder;

    private UserDTO authenticatedUser;
    
    /**
     * Constructor de la clase UserServiceImpl.
     *
     * @param userRepository Repositorio de usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     */
    @Autowired
    public UserServiceImpl(UserRepositoryI userRepository, PasswordEncoder passwordEncoder) {
    	this.userRepository = userRepository;
    	this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userName Nombre de usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param description Descripción opcional del usuario.
     * @return DTO del usuario registrado.
     */
    @Override
    public UserDTO registerUser(String userName, String email, String password, String description) {
        if (userRepository.existsByUserNameOrEmail(userName, email)) {
            throw new RuntimeException("Nombre de usuario o correo electrónico ya en uso");
        }

        validatePassword(password);

        User userEntity = new User();
        userEntity.setUserName(userName);
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setDescription(description);

        java.util.Date currentDate = new java.util.Date();
        userEntity.setCreationDate(new Date(currentDate.getTime()));

        userRepository.save(userEntity);

        return convertToDTO(userEntity);
    }
    
    /**
     * Valida la contraseña del usuario.
     *
     * @param password Contraseña a validar.
     */
    private void validatePassword(String password) {

        if (password.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

    }

    /**
     * Inicia sesión para el usuario con el nombre de usuario dado y la contraseña proporcionada.
     *
     * @param username Nombre de usuario del usuario que intenta iniciar sesión.
     * @param password Contraseña del usuario que intenta iniciar sesión.
     * @return DTO del usuario autenticado.
     */
    @Override
    public UserDTO loginUser(String username, String password) {
        User userEntity = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!isPasswordValid(password, userEntity.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        authenticatedUser = convertToDTO(userEntity);

        return authenticatedUser;
    }

    /**
     * Verifica si la contraseña proporcionada coincide con la contraseña almacenada.
     *
     * @param rawPassword Contraseña sin cifrar.
     * @param encodedPassword Contraseña cifrada almacenada.
     * @return true si la contraseña es válida, false de lo contrario.
     */
    private boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Obtiene el usuario autenticado.
     *
     * @return DTO del usuario autenticado.
     */
    @Override
    public UserDTO getAuthenticatedUser() {
        if (authenticatedUser == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return authenticatedUser;
    }

    /**
     * Edita la descripción del usuario con el ID dado.
     *
     * @param userId ID del usuario cuya descripción se va a editar.
     * @param description Nueva descripción del usuario.
     * @return DTO del usuario actualizado.
     */
    @Override
    public UserDTO editDescription(Long userId, String description) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        userEntity.setDescription(description);

        userRepository.save(userEntity);

        return convertToDTO(userEntity);
    }

    /**
     * Obtiene la lista de usuarios seguidos por el usuario con el ID dado.
     *
     * @param userId ID del usuario.
     * @return Lista de DTO de usuarios seguidos.
     */
    @Override
    public List<UserDTO> getUsersFollowedByUser(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return userEntity.getFollowers().stream()
                .map(follow -> convertToDTO(follow.getFollower()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de usuarios que siguen al usuario con el ID dado.
     *
     * @param userId ID del usuario.
     * @return Lista de DTO de usuarios que siguen.
     */
    @Override
    public List<UserDTO> getUsersFollowingUser(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return userEntity.getFollowing().stream()
                .map(follow -> convertToDTO(follow.getFollowed()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el DTO del usuario con el nombre de usuario dado.
     *
     * @param username Nombre de usuario del usuario a buscar.
     * @return DTO del usuario encontrado.
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        User userEntity = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado " + username));

        return convertToDTO(userEntity);
    }
    
    /**
     * Obtiene el DTO del usuario con el ID dado.
     *
     * @param userId ID del usuario a buscar.
     * @return DTO del usuario encontrado.
     */
    @Override
    public UserDTO getUserByUserId(Long userId) {
    	User userEntity = userRepository.findById(userId)
    			.orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
    	
    	return convertToDTO(userEntity);
    }

    /**
     * Edita los detalles del usuario con el ID dado, como la descripción y/o el correo electrónico.
     *
     * @param userId ID del usuario cuyos detalles se van a editar.
     * @param newDescription Nueva descripción del usuario.
     * @param newEmail Nuevo correo electrónico del usuario.
     * @return Mapa que contiene las claves "newDescription" y/o "newEmail" según sea necesario.
     */
    @Override
    @Transactional
    public Map<String, String> editUserDetails(Long userId, String newDescription, String newEmail) {
        Map<String, String> result = new HashMap<>();

        try {
            User userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (newDescription != null) {
                userEntity.setDescription(newDescription);
                result.put("newDescription", newDescription);
            }

            if (newEmail != null) {
                userEntity.setEmail(newEmail);
                result.put("newEmail", newEmail);
            }

            userRepository.save(userEntity);

            return result;
        } catch (Exception e) {
            log.error("Error updating user details.", e);
            throw new RuntimeException("Error updating user details.", e);
        }
    }
    
    /**
     * Obtiene una lista de todos los usuarios en el sistema.
     *
     * @return Lista de DTO de todos los usuarios.
     */
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cierra la sesión del usuario autenticado.
     */
    @Override
    public void logoutUser() {
        authenticatedUser = null;
    }

    /**
     * Convierte un objeto User en un DTO de usuario.
     *
     * @param userEntity Objeto User a convertir.
     * @return DTO del usuario.
     */
    @Override
	public UserDTO convertToDTO(User userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setDescription(userEntity.getDescription());
        userDTO.setCreationDate(userEntity.getCreationDate());
        return userDTO;
    }
}
