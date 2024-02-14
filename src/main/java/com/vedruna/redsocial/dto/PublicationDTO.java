package com.vedruna.redsocial.dto;

import com.vedruna.redsocial.persistence.model.Publication;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) que representa una publicación en la red social.
 */
@Getter
@Setter
public class PublicationDTO {

    private Long publicationId;
    private Long authorId;
    private String text;
    private String imageURL;
    private LocalDateTime creationDate;
    private LocalDateTime editionDate;

    /**
     * Convierte una entidad Publication a un objeto PublicationDTO.
     *
     * @param publication Entidad Publication a convertir.
     * @return Objeto PublicationDTO creado a partir de la entidad Publication.
     */
    public static PublicationDTO fromEntity(Publication publication) {
        PublicationDTO publicationDTO = new PublicationDTO();
        publicationDTO.setPublicationId(publication.getPublicationId());
        publicationDTO.setAuthorId(publication.getAuthor().getUserId());
        publicationDTO.setText(publication.getText());
        publicationDTO.setImageURL(publication.getImageURL());
        publicationDTO.setCreationDate(publication.getCreationDate());
        publicationDTO.setEditionDate(publication.getEditionDate());
        return publicationDTO;
    }
}
