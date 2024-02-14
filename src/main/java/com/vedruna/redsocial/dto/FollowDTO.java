package com.vedruna.redsocial.dto;

import com.vedruna.redsocial.persistence.model.Follow;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) que representa una relación de seguimiento en la red social.
 */
@Getter
@Setter
public class FollowDTO {

    private Long followID;
    private UserDTO follower;
    private UserDTO followed;

    /**
     * Convierte una entidad Follow a un objeto FollowDTO.
     *
     * @param follow Entidad Follow a convertir.
     * @return Objeto FollowDTO creado a partir de la entidad Follow.
     */
    public static FollowDTO fromEntity(Follow follow) {
        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollowID(follow.getFollowId());
        followDTO.setFollower(UserDTO.fromEntity(follow.getFollower()));
        followDTO.setFollowed(UserDTO.fromEntity(follow.getFollowed()));
        return followDTO;
    }
}
