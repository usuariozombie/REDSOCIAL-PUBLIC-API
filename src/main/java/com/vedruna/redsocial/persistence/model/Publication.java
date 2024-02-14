package com.vedruna.redsocial.persistence.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa una publicación en la red social.
 * 
 * Esta clase está mapeada a la tabla "RS_PUBLICATION" en la base de datos.
 */
@Entity
@Table(name = "RS_PUBLICATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la publicación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RS_PUBLICATION_ID")
    private Long publicationId;

    /**
     * Autor de la publicación.
     */
    @JsonBackReference(value = "author-publication")
    @ManyToOne
    @JoinColumn(name = "RS_PUBLICATION_AUTHOR", nullable = false)
    private User author;

    /**
     * Texto de la publicación.
     */
    @Column(name = "RS_PUBLICATION_TEXT")
    private String text;
    
    /**
     * URL de la imagen asociada a la publicación.
     */
    @Column(name = "RS_PUBLICATION_IMAGE")
    private String imageURL;

    /**
     * Fecha de creación de la publicación.
     */
    @Column(name = "RS_PUBLICATION_CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    /**
     * Fecha de edición de la publicación.
     */
    @Column(name = "RS_PUBLICATION_EDITION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime editionDate;
    
    /**
     * Lista de comentarios asociados a la publicación.
     */
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
