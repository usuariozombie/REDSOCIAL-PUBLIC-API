package com.vedruna.redsocial.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vedruna.redsocial.persistence.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define operaciones de acceso a datos para la entidad User en la base de datos.
 */
@Repository
public interface UserRepositoryI extends JpaRepository<User, Long> {

    /**
     * Verifica si existe un usuario con el nombre de usuario o correo electrónico dado.
     *
     * @param userName Nombre de usuario a verificar.
     * @param email    Correo electrónico a verificar.
     * @return Verdadero si existe un usuario con el nombre de usuario o correo electrónico, falso de lo contrario.
     */
    boolean existsByUserNameOrEmail(String userName, String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar.
     * @return Un objeto Optional que contiene el usuario si se encuentra, o un Optional vacío de lo contrario.
     */
    Optional<User> findByUserName(String username);

    /**
     * Obtiene una lista de usuarios seguidos por un usuario dado.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de usuarios seguidos por el usuario especificado.
     */
    @Query("SELECT u FROM User u JOIN u.following f WHERE f.follower.userId = :userId")
    List<User> findByFollowingUserId(Long userId);

    /**
     * Obtiene una lista de usuarios que siguen a un usuario dado.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de usuarios que siguen al usuario especificado.
     */
    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.followed.userId = :userId")
    List<User> findByFollowersUserId(Long userId);
    
    /**
     * Obtiene todos los usuarios.
     *
     * @return Lista de todos los usuarios en la base de datos.
     */
    List<User> findAll();
}
