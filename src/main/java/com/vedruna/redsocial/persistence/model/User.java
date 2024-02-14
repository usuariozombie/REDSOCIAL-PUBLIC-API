package com.vedruna.redsocial.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Clase que representa a un usuario en la red social.
 * 
 * Esta clase está mapeada a la tabla "RS_USER" en la base de datos.
 */
@Entity
@Table(name="RS_USER")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RS_USER_ID")
    private Long userId;

    /**
     * Nombre de usuario único.
     */
    @Column(name="RS_USER_NAME", unique = true)
    private String userName;

    /**
     * Dirección de correo electrónico única asociada al usuario.
     */
    @Column(name="RS_USER_EMAIL", unique = true)
    private String email;

    /**
     * Contraseña del usuario.
     */
    @Column(name="RS_USER_PASSWORD")
    private String password;

    /**
     * Descripción del usuario.
     */
    @Column(name="RS_USER_DESCRIPTION")
    private String description;

    /**
     * Fecha de creación del usuario.
     */
    @Column(name="RS_USER_CREATION_DATE")
    private Date creationDate;

    /**
     * Publicaciones realizadas por el usuario.
     */
    @JsonManagedReference(value="author-publication")
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Publication> publications;

    /**
     * Conjunto de seguidores del usuario.
     */
    @JsonManagedReference(value="user-followed")
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL)
    private Set<Follow> followers = new HashSet<>();

    /**
     * Conjunto de usuarios que sigue el usuario.
     */
    @JsonManagedReference(value="user-follower")
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private Set<Follow> following = new HashSet<>();

    /**
     * Lista de comentarios realizados por el usuario.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
