package com.vedruna.redsocial.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Clase que representa una relación de seguimiento entre usuarios en la red social.
 * 
 * Esta clase está mapeada a la tabla "RS_FOLLOW" en la base de datos.
 */
@Entity
@Table(name="RS_FOLLOW")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la relación de seguimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RS_FOLLOW_ID")
    private Long followId;

    /**
     * Usuario que realiza el seguimiento.
     */
    @JsonBackReference(value="user-follower")
    @ManyToOne
    @JoinColumn(name = "RS_FOLLOW_FOLLOWER", nullable = false)
    private User follower;

    /**
     * Usuario que es seguido.
     */
    @JsonBackReference(value="user-followed")
    @ManyToOne
    @JoinColumn(name = "RS_FOLLOW_FOLLOWED", nullable = false)
    private User followed;
}
