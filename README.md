# Red Social API en Spring

Este es el README para la API de la Red Social desarrollada en Spring. La aplicación utiliza el framework Spring para Java y se centra en la persistencia de datos relacionados con usuarios, publicaciones, seguimientos y comentarios.

## Persistencia de Datos

### Usuario (User)

La clase `User` representa a un usuario en la red social y está mapeada a la tabla "RS_USER" en la base de datos.

```java
@Entity
@Table(name="RS_USER")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User implements Serializable {
    // ... (atributos)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RS_USER_ID")
    private Long userId;

    // ... (resto de atributos)

    @JsonManagedReference(value="author-publication")
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Publication> publications;

    @JsonManagedReference(value="user-followed")
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL)
    private Set<Follow> followers = new HashSet<>();

    @JsonManagedReference(value="user-follower")
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
```

### Publicación (Publication)

La clase `Publication` representa una publicación en la red social y está mapeada a la tabla "RS_PUBLICATION" en la base de datos.

```java
@Entity
@Table(name = "RS_PUBLICATION")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Publication implements Serializable {
    // ... (atributos)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RS_PUBLICATION_ID")
    private Long publicationId;

    // ... (resto de atributos)

    @JsonBackReference(value = "author-publication")
    @ManyToOne
    @JoinColumn(name = "RS_PUBLICATION_AUTHOR", nullable = false)
    private User author;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
```

### Seguimiento (Follow)

La clase `Follow` representa una relación de seguimiento entre usuarios y está mapeada a la tabla "RS_FOLLOW" en la base de datos.

```java
@Entity
@Table(name="RS_FOLLOW")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Follow implements Serializable {
    // ... (atributos)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RS_FOLLOW_ID")
    private Long followId;

    // ... (resto de atributos)

    @JsonBackReference(value="user-follower")
    @ManyToOne
    @JoinColumn(name = "RS_FOLLOW_FOLLOWER", nullable = false)
    private User follower;

    @JsonBackReference(value="user-followed")
    @ManyToOne
    @JoinColumn(name = "RS_FOLLOW_FOLLOWED", nullable = false)
    private User followed;
}
```

### Comentario (Comment)

La clase `Comment` representa un comentario en la red social y está mapeada a la tabla "RS_COMMENT" en la base de datos.

```java
@Entity
@Table(name = "RS_COMMENT")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Comment implements Serializable {
    // ... (atributos)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RS_COMMENT_ID")
    private Long commentId;

    // ... (resto de atributos)

    @ManyToOne
    @JoinColumn(name = "RS_COMMENT_USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "RS_COMMENT_PUBLICATION_ID", nullable = false)
    private Publication publication;
}
```

Estas clases proporcionan la estructura para la persistencia de datos en la base de datos asociada a la aplicación. La relación entre las entidades se gestiona mediante anotaciones JPA, permitiendo un fácil manejo de los datos relacionados en la aplicación.

## Repositorios

### CommentRepository

La interfaz `CommentRepository` define operaciones de acceso a datos para la entidad `Comment` en la base de datos.

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Obtiene una lista de comentarios asociados a una publicación específica.
     *
     * @param publicationId Identificador único de la publicación.
     * @return Lista de comentarios asociados a la publicación especificada.
     */
    List<Comment> findByPublicationPublicationId(Long publicationId);
}
```

### FollowRepository

La interfaz `FollowRepository` define operaciones de acceso a datos para la entidad `Follow` en la base de datos.

```java
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * Elimina una relación de seguimiento entre un seguidor y un seguido.
     *
     * @param followerId Identificador único del seguidor.
     * @param followedId Identificador único del seguido.
     */
    @Transactional
    @Modifying
    void deleteByFollowerUserIdAndFollowedUserId(Long followerId, Long followedId);
    
    // ... (resto de métodos)
}
```

### PublicationRepository

La interfaz `PublicationRepository` define operaciones de acceso a datos para la entidad `Publication` en la base de datos.

```java
@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    /**
     * Obtiene una lista de publicaciones realizadas por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @return Lista de publicaciones realizadas por el usuario especificado.
     */
    List<Publication> findByAuthorUserId(Long userId);

    // ... (resto de métodos)
}
```

### UserRepository

La interfaz `UserRepository` define operaciones de acceso a datos para la entidad `User` en la base de datos.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Verifica si existe un usuario con el nombre de usuario o correo electrónico dado.
     *
     * @param userName Nombre de usuario a verificar.
     * @param email    Correo electrónico a verificar.
     * @return Verdadero si existe un usuario con el nombre de usuario o correo electrónico, falso de lo contrario.
     */
    boolean existsByUserNameOrEmail(String userName, String email);

    // ... (resto de métodos)
}
```

## DTOs (Data Transfer Objects)

Los DTOs (Data Transfer Objects) son clases que se utilizan para transferir datos entre las capas de la aplicación, especialmente entre la capa de servicios y la capa de controladores. Estos objetos ayudan a encapsular la información y a evitar exponer la estructura interna de las entidades de la base de datos directamente a la capa de presentación. Aquí se proporcionan detalles sobre los DTOs utilizados en tu aplicación.

### CommentDTO

El `CommentDTO` representa un comentario en la red social. Contiene información relevante para mostrar o enviar datos relacionados con los comentarios.

```java
@Getter
@Setter
public class CommentDTO {
    // ... (atributos)

    /**
     * Convierte este objeto CommentDTO a una entidad Comment.
     *
     * @param user       Usuario que realiza el comentario.
     * @param publication Publicación a la que se realiza el comentario.
     * @return Objeto Comment creado a partir de este CommentDTO.
     */
    public Comment toEntity(User user, Publication publication) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPublication(publication);
        comment.setText(text);
        comment.setCreationDate(LocalDateTime.now());
        return comment;
    }
}
```

### FollowDTO

El `FollowDTO` representa una relación de seguimiento en la red social. Se utiliza para transferir información sobre las relaciones de seguimiento.

```java
@Getter
@Setter
public class FollowDTO {
    // ... (atributos)

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
```

### PublicationDTO

El `PublicationDTO` representa una publicación en la red social. Contiene información relevante para mostrar o enviar datos relacionados con las publicaciones.

```java
@Getter
@Setter
public class PublicationDTO {
    // ... (atributos)

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
```

### UserDTO

El `UserDTO` representa a un usuario en la red social. Contiene información relevante para mostrar o enviar datos relacionados con los usuarios.

```java
@Getter
@Setter
public class UserDTO {
    // ... (atributos)

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
```

Estos DTOs proporcionan una capa de abstracción que facilita la transferencia de datos entre las diferentes capas de la aplicación, manteniendo la flexibilidad y la seguridad al evitar la exposición innecesaria de información interna.

## Servicios

### CommentServiceImpl

```java
/**
 * Implementación de la interfaz CommentServiceI que proporciona servicios relacionados con los comentarios en la red social.
 */
@Service
public class CommentServiceImpl implements CommentServiceI {

    // ... (atributos)

    /**
     * Constructor de la clase CommentServiceImpl.
     *
     * @param commentRepository     Repositorio de comentarios.
     * @param userRepository        Repositorio de usuarios.
     * @param publicationRepository Repositorio de publicaciones.
     * @param userService           Servicio de usuarios.
     */
    @Autowired
    public CommentServiceImpl(
        CommentRepositoryI commentRepository,
        UserRepositoryI userRepository,
        PublicationRepositoryI publicationRepository,
        UserServiceI userService) {
        // ... (inicialización de atributos)
    }

    // Métodos y operaciones del servicio
}
```

#### Métodos Principales:

1. **getCommentsByPublicationId(Long publicationId): List<CommentDTO>**
   - Descripción: Obtiene una lista de comentarios asociados a una publicación específica.
   - Parámetros:
     - `publicationId`: Identificador único de la publicación.
   - Devuelve: Lista de DTO que representan los comentarios asociados a la publicación especificada.

2. **addComment(Long userId, Long publicationId, CommentDTO commentDTO): CommentDTO**
   - Descripción: Agrega un comentario a una publicación.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza el comentario.
     - `publicationId`: Identificador único de la publicación a la que se realiza el comentario.
     - `commentDTO`: DTO que representa el comentario a agregar.
   - Devuelve: DTO que representa el comentario agregado.

#### Métodos Secundarios:

1. **convertToDTO(Comment commentEntity): CommentDTO**
   - Descripción: Convierte una entidad Comment a un objeto CommentDTO.
   - Parámetros:
     - `commentEntity`: Entidad Comment a convertir.
   - Devuelve: Objeto CommentDTO creado a partir de la entidad Comment.

### PublicationServiceImpl

```java
/**
 * Implementación de la interfaz PublicationServiceI que proporciona servicios relacionados con las publicaciones en la red social.
 */
@Service
public class PublicationServiceImpl implements PublicationServiceI {

    // ... (atributos)

    /**
     * Constructor de la clase PublicationServiceImpl.
     *
     * @param publicationRepository Repositorio de publicaciones.
     * @param userRepository        Repositorio de usuarios.
     * @param userService           Servicio de usuarios.
     * @param followService         Servicio de relaciones de seguimiento.
     */
    @Autowired
    public PublicationServiceImpl(PublicationRepositoryI publicationRepository, UserRepositoryI userRepository,
    		UserServiceI userService, FollowServiceI followService) {
    	// ... (inicialización de atributos)
    }

    // Métodos y operaciones del servicio
}
```

#### Métodos Principales:

1. **getAllPublications(): List<PublicationDTO>**
   - Descripción: Obtiene todas las publicaciones en la red social.
   - Devuelve: Lista de DTO que representan todas las publicaciones.

2. **getPublicationsByUser(Long userId): List<PublicationDTO>**
   - Descripción: Obtiene las publicaciones de un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO que representan las publicaciones del usuario.

3. **getPublicationsByUsersFollowed(Long userId): List<PublicationDTO>**
   - Descripción: Obtiene las publicaciones de los usuarios seguidos por un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO que representan las publicaciones de los usuarios seguidos.

4. **createPublication(Long userId, PublicationDTO publicationDTO): PublicationDTO**
   - Descripción: Crea una nueva publicación para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la publicación.
     - `publicationDTO`: DTO que representa la nueva publicación.
   - Devuelve: DTO que representa la publicación creada.

5. **editPublication(Long userId, Long publicationId, PublicationDTO publicationDTO): PublicationDTO**
   - Descripción: Edita una publicación existente para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la edición.
     - `publicationId`: Identificador único de la publicación a editar.
     - `publicationDTO`: DTO que representa la publicación editada.
   - Devuelve: DTO que representa la publicación editada.

6. **deletePublication(Long userId, Long publicationId): void**
   - Descripción: Elimina una publicación para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la eliminación.
     - `publicationId`: Identificador único de la publicación a eliminar.

7. **getPublicationByPublicationId(Long publicationId): PublicationDTO**
   - Descripción: Obtiene una publicación por su identificador único.
   - Parámetros:
     - `publicationId`: Identificador único de la publicación.
   - Devuelve: DTO que representa la publicación.

8

. **convertToDTO(Publication publicationEntity): PublicationDTO**
   - Descripción: Convierte una entidad Publication a un DTO PublicationDTO.
   - Parámetros:
     - `publicationEntity`: La entidad Publication a convertir.
   - Devuelve: DTO PublicationDTO que representa la publicación convertida.

### UserServiceImpl

```java
/**
 * Implementación de la interfaz UserServiceI que proporciona servicios relacionados con la gestión de usuarios.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserServiceI {

    // ... (atributos)

    /**
     * Constructor de la clase UserServiceImpl.
     *
     * @param userRepository Repositorio de usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     */
    @Autowired
    public UserServiceImpl(UserRepositoryI userRepository, PasswordEncoder passwordEncoder) {
    	// ... (inicialización de atributos)
    }

    // Métodos y operaciones del servicio
}
```

#### Métodos Principales:

1. **registerUser(String userName, String email, String password, String description): UserDTO**
   - Descripción: Registra un nuevo usuario en el sistema.
   - Parámetros:
     - `userName`: Nombre de usuario.
     - `email`: Correo electrónico del usuario.
     - `password`: Contraseña del usuario.
     - `description`: Descripción opcional del usuario.
   - Devuelve: DTO del usuario registrado.

2. **loginUser(String username, String password): UserDTO**
   - Descripción: Inicia sesión para el usuario con el nombre de usuario dado y la contraseña proporcionada.
   - Parámetros:
     - `username`: Nombre de usuario del usuario que intenta iniciar sesión.
     - `password`: Contraseña del usuario que intenta iniciar sesión.
   - Devuelve: DTO del usuario autenticado.

3. **getAuthenticatedUser(): UserDTO**
   - Descripción: Obtiene el usuario autenticado.
   - Devuelve: DTO del usuario autenticado.

4. **editDescription(Long userId, String description): UserDTO**
   - Descripción: Edita la descripción del usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario cuya descripción se va a editar.
     - `description`: Nueva descripción del usuario.
   - Devuelve: DTO del usuario actualizado.

5. **getUsersFollowedByUser(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios seguidos por el usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario.
   - Devuelve: Lista de DTO de usuarios seguidos.

6. **getUsersFollowingUser(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios que siguen al usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario.
   - Devuelve: Lista de DTO de usuarios que siguen.

7. **getUserByUsername(String username): UserDTO**
   - Descripción: Obtiene el DTO del usuario con el nombre de usuario dado.
   - Parámetros:
     - `username`: Nombre de usuario del usuario a buscar.
   - Devuelve: DTO del usuario encontrado.

8. **getUserByUserId(Long userId): UserDTO**
   - Descripción: Obtiene el DTO del usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario a buscar.
   - Devuelve: DTO del usuario encontrado.

9. **editUserDetails(Long userId, String newDescription, String newEmail): Map<String, String>**
   - Descripción: Edita los detalles del usuario con el ID dado, como la descripción y/o el correo electrónico.
   - Parámetros:
     - `userId`: ID del usuario cuyos detalles se van a editar.
     - `newDescription`: Nueva descripción del usuario.
     - `newEmail`: Nuevo correo electrónico del usuario.
   - Devuelve: Mapa que contiene las claves "newDescription" y/o "newEmail" según sea necesario.

10. **getAllUsers(): List<UserDTO>**
    - Descripción: Obtiene una lista de todos los usuarios en el sistema.
    - Devuelve: Lista de DTO de todos los usuarios.

11. **logoutUser(): void**
    - Descripción: Cierra la sesión del usuario autenticado.

12. **convertToDTO(User userEntity): UserDTO**
    - Descripción: Convierte un objeto User en un DTO de usuario.
    - Parámetros:
      - `userEntity`: Objeto User a convertir.
    - Devuelve: DTO del usuario.

### Comentarios Generales

- Los servicios están correctamente anotados con `@Service`.
- Se utilizan anotaciones `@Autowired` para la inyección de dependencias.
- Se manejan excepciones para casos como usuario no encontrado o credenciales incorrectas.
- Existen métodos que realizan operaciones de CRUD para comentarios y publicaciones.
- El servicio de usuario maneja operaciones de registro, inicio de sesión, edición de detalles y obtención de listas de usuarios.
- Se proporcionan métodos para obtener información específica de usuarios, como los seguidos y seguidores.
- Las fechas y las validaciones de contraseña están implementadas adecuadamente.
- Se utiliza el patrón DTO para transferir datos entre las capas.

### CommentServiceImpl

#### Métodos Principales:

1. **getCommentsByPublicationId(Long publicationId): List<CommentDTO>**
   - Descripción: Obtiene una lista de comentarios asociados a una publicación específica.
   - Parámetros:
     - `publicationId`: Identificador único de la publicación.
   - Devuelve: Lista de DTO que representan los comentarios asociados a la publicación especificada.

2. **addComment(Long userId, Long publicationId, CommentDTO commentDTO): CommentDTO**
   - Descripción: Agrega un comentario a una publicación.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza el comentario.
     - `publicationId`: Identificador único de la publicación a la que se realiza el comentario.
     - `commentDTO`: DTO que representa el comentario a agregar.
   - Devuelve: DTO que representa el comentario agregado.

#### Comentarios Generales:

- El servicio implementa operaciones relacionadas con los comentarios en la red social.
- Se utiliza la anotación `@Service` para la inyección de dependencias.
- Se manejan excepciones para casos como usuario no autorizado, usuario no encontrado y publicación no encontrada.
- Se proporcionan métodos para obtener comentarios por ID de publicación y agregar nuevos comentarios.
- Las fechas de creación se manejan correctamente.

### PublicationServiceImpl

#### Métodos Principales:

1. **getAllPublications(): List<PublicationDTO>**
   - Descripción: Obtiene todas las publicaciones en la red social.
   - Devuelve: Lista de DTO que representan todas las publicaciones.

2. **getPublicationsByUser(Long userId): List<PublicationDTO>**
   - Descripción: Obtiene las publicaciones de un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO que representan las publicaciones del usuario.

3. **getPublicationsByUsersFollowed(Long userId): List<PublicationDTO>**
   - Descripción: Obtiene las publicaciones de los usuarios seguidos por un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO que representan las publicaciones de los usuarios seguidos.

4. **createPublication(Long userId, PublicationDTO publicationDTO): PublicationDTO**
   - Descripción: Crea una nueva publicación para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la publicación.
     - `publicationDTO`: DTO que representa la nueva publicación.
   - Devuelve: DTO que representa la publicación creada.

5. **editPublication(Long userId, Long publicationId, PublicationDTO publicationDTO): PublicationDTO**
   - Descripción: Edita una publicación existente para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la edición.
     - `publicationId`: Identificador único de la publicación a editar.
     - `publicationDTO`: DTO que representa la publicación editada.
   - Devuelve: DTO que representa la publicación editada.

6. **deletePublication(Long userId, Long publicationId): void**
   - Descripción: Elimina una publicación para un usuario específico.
   - Parámetros:
     - `userId`: Identificador único del usuario que realiza la eliminación.
     - `publicationId`: Identificador único de la publicación a eliminar.

7. **getPublicationByPublicationId(Long publicationId): PublicationDTO**
   - Descripción: Obtiene una publicación por su identificador único.
   - Parámetros:
     - `publicationId`: Identificador único de la publicación.
   - Devuelve: DTO que representa la publicación.

#### Comentarios Generales:

- El servicio implementa operaciones relacionadas con las publicaciones en la red social.
- Se utiliza la anotación `@Service` para la inyección de dependencias.
- Se manejan excepciones para casos como usuario no autorizado, usuario no encontrado y publicación no encontrada.
- Se proporcionan métodos para realizar operaciones CRUD en publicaciones.
- Se manejan fechas y edición de publicaciones de manera adecuada.

### UserServiceImpl

#### Métodos Principales:

1. **registerUser(String userName, String email, String password, String description): UserDTO**
   - Descripción: Registra un nuevo usuario en el sistema.
   - Parámetros:
     - `userName`: Nombre de usuario.
     - `email`: Correo electrónico del usuario.
     - `password`: Contraseña del usuario.
     - `description`: Descripción opcional del usuario.
   - Devuelve: DTO del usuario registrado.

2. **loginUser(String username, String password): UserDTO**
   - Descripción: Inicia sesión para el usuario con el nombre de usuario dado y la contraseña proporcionada.
   - Parámetros:
     - `username`: Nombre de usuario del usuario que intenta iniciar sesión.
     - `password`: Contraseña del usuario que intenta iniciar sesión.
   - Devuelve: DTO del usuario autenticado.

3. **getAuthenticatedUser(): UserDTO**
   - Descripción: Obtiene el usuario autenticado.
   - Devuelve: DTO del usuario autenticado.

4. **editDescription(Long userId, String description): UserDTO**
   - Descripción: Edita la descripción del usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario cuya descripción se va a editar.
     - `description`: Nueva descripción del usuario.
   - Devuelve: DTO del usuario actualizado.

5. **getUsersFollowedByUser(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios seguidos por el usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario.
   - Devuelve: Lista de DTO de usuarios seguidos.

6. **getUsersFollowingUser(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios que siguen al usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario.
   - Devuelve: Lista de DTO de usuarios que siguen.

7. **getUserByUsername(String username): UserDTO**
   - Descripción: Obtiene el DTO del usuario con el nombre de usuario dado.
   - Parámetros:
     - `username`: Nombre de usuario del usuario a buscar.
   - Devuelve: DTO del usuario encontrado.

8. **getUserByUserId(Long userId): UserDTO**
   - Descripción: Obtiene el DTO del usuario con el ID dado.
   - Parámetros:
     - `userId`: ID del usuario a buscar.
   - Devuelve: DTO del usuario encontrado.

9. **editUserDetails(Long userId, String newDescription,

 String newEmail): Map<String, String>**
   - Descripción: Edita los detalles del usuario con el ID dado, como la descripción y/o el correo electrónico.
   - Parámetros:
     - `userId`: ID del usuario cuyos detalles se van a editar.
     - `newDescription`: Nueva descripción del usuario.
     - `newEmail`: Nuevo correo electrónico del usuario.
   - Devuelve: Mapa que contiene las claves "newDescription" y/o "newEmail" según sea necesario.

10. **getAllUsers(): List<UserDTO>**
    - Descripción: Obtiene una lista de todos los usuarios en el sistema.
    - Devuelve: Lista de DTO de todos los usuarios.

11. **logoutUser(): void**
    - Descripción: Cierra la sesión del usuario autenticado.

12. **convertToDTO(User userEntity): UserDTO**
    - Descripción: Convierte un objeto User en un DTO de usuario.
    - Parámetros:
      - `userEntity`: Objeto User a convertir.
    - Devuelve: DTO del usuario.

### FollowServiceImpl

#### Métodos Principales:

1. **followUser(Long followerId, Long followeeId): FollowDTO**
   - Descripción: Permite a un usuario seguir a otro usuario.
   - Parámetros:
     - `followerId`: Identificador único del usuario que realiza el seguimiento.
     - `followeeId`: Identificador único del usuario que es seguido.
   - Devuelve: DTO que representa la relación de seguimiento creada.

2. **unfollowUser(Long followerId, Long followeeId): void**
   - Descripción: Permite a un usuario dejar de seguir a otro usuario.
   - Parámetros:
     - `followerId`: Identificador único del usuario que deja de seguir.
     - `followeeId`: Identificador único del usuario que deja de ser seguido.

3. **getFollowers(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios que siguen al usuario con el ID dado.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO de usuarios que siguen al usuario especificado.

4. **getFollowing(Long userId): List<UserDTO>**
   - Descripción: Obtiene la lista de usuarios a los que sigue el usuario con el ID dado.
   - Parámetros:
     - `userId`: Identificador único del usuario.
   - Devuelve: Lista de DTO de usuarios a los que sigue el usuario especificado.

5. **isFollowing(Long followerId, Long followeeId): Boolean**
   - Descripción: Verifica si un usuario sigue a otro usuario.
   - Parámetros:
     - `followerId`: Identificador único del usuario que podría seguir.
     - `followeeId`: Identificador único del usuario que podría ser seguido.
   - Devuelve: `true` si el usuario sigue al otro, `false` de lo contrario.

#### Comentarios Generales:

- El servicio implementa operaciones relacionadas con la gestión de usuarios.
- Se utiliza la anotación `@Service` para la inyección de dependencias.
- Se manejan excepciones para casos como usuario no encontrado, contraseña incorrecta y nombre de usuario ya en uso.
- Se proporcionan métodos para realizar operaciones de registro, inicio de sesión, edición de detalles y obtención de información de usuarios.
- Se manejan fechas y descripciones de usuarios de manera adecuada.

**Readme - Controlador de Red Social**

## SocialMediaController

El `SocialMediaController` es el controlador principal encargado de gestionar las operaciones relacionadas con la red social. Proporciona endpoints para registrar usuarios, gestionar publicaciones, seguir y dejar de seguir a otros usuarios, entre otras funcionalidades.

### Endpoints Principales:

1. **Registrar Usuario**
   - Método: `POST /api/register`
   - Descripción: Registra un nuevo usuario en la red social.
   - Parámetros de Entrada: `UserDTO` con datos del usuario a registrar.
   - Respuesta Exitosa (Código 200): Devuelve el `UserDTO` del usuario registrado.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 400 (Solicitud incorrecta).

2. **Iniciar Sesión**
   - Método: `POST /api/login`
   - Descripción: Inicia sesión para un usuario en la red social.
   - Parámetros de Entrada: `UserDTO` con datos del usuario para iniciar sesión.
   - Respuesta Exitosa (Código 200): Devuelve el `UserDTO` del usuario que ha iniciado sesión.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 401 (No autorizado).

3. **Obtener Todas las Publicaciones**
   - Método: `GET /api/publication`
   - Descripción: Obtiene todas las publicaciones almacenadas en el sistema.
   - Respuesta Exitosa (Código 200): Devuelve una lista de `PublicationDTO`.
   - Posibles Respuestas de Error: 500 (Error interno del servidor).

4. **Obtener Todos los Usuarios**
   - Método: `GET /api/user/all`
   - Descripción: Obtiene todos los usuarios registrados en el sistema.
   - Respuesta Exitosa (Código 200): Devuelve una lista de `UserDTO`.
   - Posibles Respuestas de Error: 500 (Error interno del servidor).

5. **Obtener Usuario por Nombre de Usuario**
   - Método: `GET /api/profile/{username}`
   - Descripción: Obtiene un usuario por su nombre de usuario.
   - Parámetros de Entrada: `username` - Nombre de usuario.
   - Respuesta Exitosa (Código 200): Devuelve el `UserDTO` del usuario encontrado.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

6. **Obtener Usuario por ID**
   - Método: `GET /api/user/{userId}`
   - Descripción: Obtiene un usuario por su identificador único.
   - Parámetros de Entrada: `userId` - Identificador único del usuario.
   - Respuesta Exitosa (Código 200): Devuelve el `UserDTO` del usuario encontrado.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

7. **Obtener Seguidores por ID de Usuario**
   - Método: `GET /api/user/{userId}/followers`
   - Descripción: Obtiene los seguidores de un usuario por su identificador único.
   - Parámetros de Entrada: `userId` - Identificador único del usuario.
   - Respuesta Exitosa (Código 200): Devuelve una lista de `UserDTO` representando los seguidores.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

8. **Obtener Usuarios Seguidos por ID de Usuario**
   - Método: `GET /api/user/{userId}/following`
   - Descripción: Obtiene los usuarios seguidos por un usuario por su identificador único.
   - Parámetros de Entrada: `userId` - Identificador único del usuario.
   - Respuesta Exitosa (Código 200): Devuelve una lista de `UserDTO` representando los usuarios seguidos.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

9. **Seguir a un Usuario**
   - Método: `POST /api/user/{followerId}/follow/{followedId}`
   - Descripción: Realiza el seguimiento de un usuario por parte de otro.
   - Parámetros de Entrada: `followerId` - Identificador único del usuario que realiza el seguimiento, `followedId` - Identificador único del usuario que es seguido.
   - Respuesta Exitosa (Código 200): Confirmación exitosa del seguimiento.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

10. **Dejar de Seguir a un Usuario**
    - Método: `DELETE /api/user/{followerId}/unfollow/{followedId}`
    - Descripción: Deja de seguir a un usuario por parte de otro.
    - Parámetros de Entrada: `followerId` - Identificador único del usuario que deja de seguir, `followedId` - Identificador único del usuario que deja de ser seguido.
    - Respuesta Exitosa (Código 204): Confirmación exitosa de dejar de seguir.
    - Posibles Respuestas de Error: 500 (Error interno del servidor).

11. **Obtener Publicaciones por ID de Usuario**
    - Método: `GET /api/user/{userId}/publications`
    - Descripción: Obtiene las publicaciones realizadas por un usuario por su identificador único.
    - Parámetros de Entrada: `userId` - Identificador único del usuario.
    - Respuesta Exitosa (Código 200): Devuelve una lista de `PublicationDTO`.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

12. **Obtener Feed por ID de Usuario**
    - Método: `GET /api/user/{userId}/feed`
    - Descripción: Obtiene el feed de un usuario, que incluye las publicaciones de los usuarios seguidos.
    - Parámetros de Entrada: `userId` - Identificador único del usuario.
    - Respuesta Exitosa (Código 200): Devuelve una lista de `PublicationDTO` representando el feed.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

13. **Crear Nueva Publicación para un Usuario**
    - Método: `POST /api/user/{userId}/publication`
    - Descripción: Crea una nueva publicación para un usuario por su identificador único.
    - Parámetros de Entrada: `userId` - Identificador único del usuario, `publicationDTO` - Detalles de la publicación a crear.
    - Respuesta Exitosa (Código 201): Devuelve el `PublicationDTO` de la publicación creada.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 400 (Solicitud incorrecta), 404 (No encontrado).

14. **Editar

 Publicación Existente**
    - Método: `PUT /api/user/{userId}/publication/{publicationId}`
    - Descripción: Edita una publicación existente.
    - Parámetros de Entrada: `userId` - Identificador único del usuario, `publicationId` - Identificador único de la publicación a editar, `publicationDTO` - Nuevos detalles de la publicación.
    - Respuesta Exitosa (Código 200): Devuelve el `PublicationDTO` de la publicación editada.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

15. **Eliminar Publicación Existente**
    - Método: `DELETE /api/user/{userId}/publication/{publicationId}`
    - Descripción: Elimina una publicación existente.
    - Parámetros de Entrada: `userId` - Identificador único del usuario, `publicationId` - Identificador único de la publicación a eliminar.
    - Respuesta Exitosa (Código 204): Confirmación exitosa de la eliminación.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

16. **Obtener Publicación por ID**
    - Método: `GET /api/publication/{publicationId}`
    - Descripción: Obtiene una publicación por su identificador único.
    - Parámetros de Entrada: `publicationId` - Identificador único de la publicación.
    - Respuesta Exitosa (Código 200): Devuelve el `PublicationDTO` de la publicación encontrada.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

17. **Editar Detalles de Usuario**
    - Método: `PUT /api/{userId}/edit/details`
    - Descripción: Edita los detalles de un usuario.
    - Parámetros de Entrada: `userId` - Identificador único del usuario, `userDetails` - Nuevos detalles del usuario.
    - Respuesta Exitosa (Código 200): Devuelve un `Map` con detalles actualizados.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 400 (Solicitud incorrecta), 404 (No encontrado), 409 (Conflicto - Email ya existe).

18. **Obtener Comentarios por ID de Publicación**
    - Método: `GET /api/publication/{publicationId}/comments`
    - Descripción: Obtiene todos los comentarios asociados a una publicación.
    - Parámetros de Entrada: `publicationId` - Identificador de la publicación.
    - Respuesta Exitosa (Código 200): Devuelve una lista de `CommentDTO`.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

19. **Agregar Comentario a una Publicación**
    - Método: `POST /api/publication/{publicationId}/comments/user/{userId}`
    - Descripción: Agrega un comentario a una publicación.
    - Parámetros de Entrada: `publicationId` - Identificador de la publicación, `userId` - Identificador del usuario que realiza el comentario, `commentDTO` - DTO que contiene la información del comentario.
    - Respuesta Exitosa (Código 200): Devuelve el `CommentDTO` del comentario agregado.
    - Posibles Respuestas de Error: 500 (Error interno del servidor), 404 (No encontrado).

---
**Controlador de Autenticación (Spring Security)**

## AuthController

El `AuthController` maneja las solicitudes relacionadas con la autenticación, como iniciar sesión (`/login`) y registrar un nuevo usuario (`/register`).

### Endpoints Principales:

1. **Iniciar Sesión**
   - Método: `POST /auth/login`
   - Descripción: Inicia sesión para un usuario.
   - Parámetros de Entrada: `LoginRequest` con nombre de usuario y contraseña.
   - Respuesta Exitosa (Código 200): Devuelve un `AuthResponse` con el token JWT.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 401 (No autorizado).

2. **Registrar Usuario**
   - Método: `POST /auth/register`
   - Descripción: Registra un nuevo usuario.
   - Parámetros de Entrada: `RegisterRequest` con detalles del nuevo usuario.
   - Respuesta Exitosa (Código 200): Devuelve un `AuthResponse` con el token JWT.
   - Posibles Respuestas de Error: 500 (Error interno del servidor), 401 (No autorizado).

### Clases y Métodos Relacionados:

- **AuthResponse:**
  - Descripción: Contiene el token JWT devuelto en las respuestas de autenticación.

- **LoginRequest:**
  - Descripción: Representa la solicitud de inicio de sesión con nombre de usuario y contraseña.

- **RegisterRequest:**
  - Descripción: Representa la solicitud de registro con detalles del nuevo usuario.

- **AuthService:**
  - Descripción: Proporciona métodos para iniciar sesión y registrar usuarios.
  - Métodos Principales:
    - `login(LoginRequest request): AuthResponse`: Inicia sesión y devuelve un token JWT.
    - `register(RegisterRequest request): AuthResponse`: Registra un nuevo usuario y devuelve un token JWT.

### Configuración y Seguridad:

- **ApplicationConfig:**
  - Descripción: Configuración de la aplicación relacionada con la autenticación.
  - Beans Principales:
    - `authenticationManager(AuthenticationConfiguration config)`: Configura el administrador de autenticación.
    - `authenticationProvider()`: Configura el proveedor de autenticación.
    - `userDetailService()`: Configura el servicio de detalles de usuario.
    - `passwordEncoder()`: Configura el codificador de contraseñas.

- **SecurityConfig:**
  - Descripción: Configuración de seguridad para la aplicación.
  - Principales Configuraciones:
    - Reglas de acceso para los endpoints de autenticación.
    - Deshabilitación de CSRF.
    - Configuración de la sesión como sin estado (STATELESS).

- **JWTAuthenticationFilter:**
  - Descripción: Filtro de autenticación basado en JWT para procesar solicitudes entrantes y realizar la autenticación si se proporciona un token válido.
  - Funcionalidades Principales:
    - Extracción del token de autenticación de la solicitud.
    - Validación del token y autenticación del usuario.

---

**Instrucciones para Ejecutar la API:**

Para poner en funcionamiento la API de autenticación con Spring Security, siga estos pasos:

### Requisitos Previos:
1. **Entorno de Desarrollo:**
   - Asegúrese de tener Java JDK 21 y Spring Boot configurados en su entorno de desarrollo.

2. **Base de Datos:**
   - Configure una base de datos compatible (por ejemplo, MySQL, PostgreSQL) y actualice la configuración en `application.properties` con las credenciales correctas.

### Pasos para Ejecutar:

1. **Clonar Repositorio:**
   - Clone el repositorio de la aplicación desde el sistema de control de versiones.

2. **Configuración de Propiedades:**
   - Actualice las propiedades de configuración en `application.properties` según los detalles de su entorno y base de datos.

3. **Ejecutar la Aplicación:**
   - Ejecute la aplicación Spring Boot desde su entorno de desarrollo o utilizando el siguiente comando en la terminal:
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Verificación de la Aplicación:**
   - Acceda a la documentación de la API Swagger desde el navegador:
     ```
     http://localhost:8080/swagger-ui/index.html
     ```

5. **Autenticación y Registro:**
   - Utilice los endpoints de autenticación (`/auth/login` y `/auth/register`) para iniciar sesión y registrar nuevos usuarios.

6. **Token JWT:**
   - Capture el token JWT devuelto en las respuestas exitosas de inicio de sesión o registro.

### Uso del Token JWT:

- **Incluir Token en Solicitudes:**
  - En solicitudes protegidas, incluya el token JWT en el encabezado de autorización como "Bearer {token}".

- **Verificación del Token:**
  - El filtro `JWTAuthenticationFilter` verifica automáticamente el token en las solicitudes entrantes.

¡La API de autenticación ahora está en funcionamiento! Para detalles adicionales sobre los endpoints y sus funciones específicas, consulte la documentación Swagger proporcionada.

---
