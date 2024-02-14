package com.vedruna.redsocial.dto;

import java.sql.Date;

import org.springframework.beans.BeanUtils;

import com.vedruna.redsocial.persistence.model.User;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) que representa a un usuario en la red social.
 */
@Getter
@Setter
public class UserDTO {

    private Long userId;
    private String userName;
    private String email;
    private String description;
    private Date creationDate;
    private String password;

    /**
     * Convierte una entidad User a un objeto UserDTO.
     *
     * @param user Entidad User a convertir.
     * @return Objeto UserDTO creado a partir de la entidad User.
     */
    public static UserDTO fromEntity(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // Excluir la propiedad "password" en el DTO de salida
        userDTO.setPassword(null);
        
        return userDTO;
    }

    /**
     * Convierte un objeto UserDTO a una entidad User.
     *
     * @param userDTO Objeto UserDTO a convertir.
     * @return Entidad User creada a partir del objeto UserDTO.
     */
    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
