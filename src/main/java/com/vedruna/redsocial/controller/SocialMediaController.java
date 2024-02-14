package com.vedruna.redsocial.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vedruna.redsocial.dto.CommentDTO;
import com.vedruna.redsocial.dto.PublicationDTO;
import com.vedruna.redsocial.dto.UserDTO;
import com.vedruna.redsocial.service.CommentServiceI;
import com.vedruna.redsocial.service.FollowServiceI;
import com.vedruna.redsocial.service.PublicationServiceI;
import com.vedruna.redsocial.service.UserServiceI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que gestiona las operaciones relacionadas con la red social.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SocialMediaController {
	
    private UserServiceI userService;

    private FollowServiceI followService;

    private PublicationServiceI publicationService;
    
    private CommentServiceI commentService;
    
    /**
     * Constructor de la clase SocialMediaController.
     *
     * @param userService         Servicio de usuario
     * @param followService       Servicio de seguidores
     * @param publicationService  Servicio de publicaciones
     * @param commentService      Servicio de comentarios
     */
    @Autowired
    public SocialMediaController(UserServiceI userService, FollowServiceI followService,
            PublicationServiceI publicationService, CommentServiceI commentService) {
        this.userService = userService;
        this.followService = followService;
        this.publicationService = publicationService;
        this.commentService = commentService;
    }
    

    /**
     * Registra un nuevo usuario en la red social.
     *
     * @param userDTO Datos del usuario a registrar
     * @return Usuario registrado
     */
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO userDTO) {
        return userService.registerUser(userDTO.getUserName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getDescription());
    }

    /**
     * Inicia sesión para un usuario en la red social.
     *
     * @param userDTO Datos del usuario para iniciar sesión
     * @return Usuario que ha iniciado sesión
     */
    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public UserDTO loginUser(@RequestBody UserDTO userDTO) {
        return userService.loginUser(userDTO.getUserName(), userDTO.getPassword());
    }

    /**
     * Obtiene todas las publicaciones almacenadas en el sistema.
     *
     * @return ResponseEntity con la lista de PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get all publications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all publications",
        		content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicationDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/publication")
    public ResponseEntity<List<PublicationDTO>> getAllPublications() {
        List<PublicationDTO> allPublications = publicationService.getAllPublications();
        return new ResponseEntity<>(allPublications, HttpStatus.OK);
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return ResponseEntity con la lista de UserDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users",
        		content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario del usuario a recuperar.
     * @return ResponseEntity con UserDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get user by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user by username",
                content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param userId Identificador único del usuario a recuperar.
     * @return ResponseEntity con UserDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
        		content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> getUserByUserId(@PathVariable(name = "userId") Long userId) {
        UserDTO user = userService.getUserByUserId(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtiene los seguidores de un usuario por su identificador único.
     *
     * @param userId Identificador único del usuario para el cual se recuperan los seguidores.
     * @return ResponseEntity con la lista de UserDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get followers by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved followers",
        		content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}/followers")
    public ResponseEntity<List<UserDTO>> getFollowersByUserId(@PathVariable(name = "userId") Long userId) {
        List<UserDTO> followers = followService.getFollowersByUserId(userId);
        if (followers != null) {
            return ResponseEntity.ok(followers);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtiene los usuarios seguidos por un usuario por su identificador único.
     *
     * @param userId Identificador único del usuario para el cual se recuperan los usuarios seguidos.
     * @return ResponseEntity con la lista de UserDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get users followed by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users followed",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}/following")
    public ResponseEntity<List<UserDTO>> getFollowingByUserId(@PathVariable(name = "userId") Long userId) {
        List<UserDTO> following = followService.getFollowingByUserId(userId);
        if (following != null) {
            return ResponseEntity.ok(following);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Realiza el seguimiento de un usuario por parte de otro.
     *
     * @param followerId Identificador único del usuario que realiza el seguimiento.
     * @param followedId Identificador único del usuario que es seguido.
     * @return ResponseEntity con el estado HTTP correspondiente.
     */
    @Operation(summary = "Follow a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully followed user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user/{followerId}/follow/{followedId}")
    public ResponseEntity<Void> followUser(@PathVariable(name = "followerId") Long followerId,
                                           @PathVariable(name = "followedId") Long followedId) {
        try {
            followService.followUser(followerId, followedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error during followUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deja de seguir a un usuario por parte de otro.
     *
     * @param followerId Identificador único del usuario que deja de seguir.
     * @param followedId Identificador único del usuario que deja de ser seguido.
     * @return ResponseEntity con el estado HTTP correspondiente.
     */
    @Operation(summary = "Unfollow a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully unfollowed user"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/user/{followerId}/unfollow/{followedId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable(name = "followerId") Long followerId,
                                             @PathVariable(name = "followedId") Long followedId) {
        try {
            followService.unfollowUser(followerId, followedId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error during unfollowUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene las publicaciones realizadas por un usuario por su identificador único.
     *
     * @param userId Identificador único del usuario para el cual se recuperan las publicaciones.
     * @return ResponseEntity con la lista de PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get publications by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved publications by user",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicationDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}/publications")
    public ResponseEntity<List<PublicationDTO>> getPublicationsByUserId(@PathVariable(name = "userId") Long userId) {
        List<PublicationDTO> publications = publicationService.getPublicationsByUser(userId);
        if (publications != null) {
            return ResponseEntity.ok(publications);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Obtiene el feed de un usuario por su identificador único, que incluye las publicaciones de los usuarios seguidos.
     *
     * @param userId Identificador único del usuario para el cual se recupera el feed.
     * @return ResponseEntity con la lista de PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get feed by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved feed",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicationDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}/feed")
    public ResponseEntity<List<PublicationDTO>> getFeedByUserId(@PathVariable(name = "userId") Long userId) {
        List<PublicationDTO> feed = publicationService.getPublicationsByUsersFollowed(userId);
        if (feed != null) {
            return ResponseEntity.ok(feed);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Crea una nueva publicación para un usuario por su identificador único.
     *
     * @param userId           Identificador único del usuario para el cual se crea la publicación.
     * @param publicationDTO   Detalles de la publicación a crear.
     * @return ResponseEntity con PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Create a new publication for user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Publication created successfully",
                content = @Content(schema = @Schema(implementation = PublicationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user/{userId}/publication")
    public ResponseEntity<PublicationDTO> createPublication(@PathVariable(name = "userId") Long userId,
                                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                   description = "Publication details",
                                                                   content = @Content(schema = @Schema(implementation = PublicationDTO.class)),
                                                                   required = true
                                                           )
                                                           @RequestBody PublicationDTO publicationDTO) {
        try {
            System.out.println("Received request to create publication for user " + userId);
            PublicationDTO createdPublication = publicationService.createPublication(userId, publicationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPublication);
        } catch (Exception e) {
            log.error("Error during createPublication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Edita una publicación existente.
     *
     * @param userId          Identificador único del usuario que realizó la publicación.
     * @param publicationId   Identificador único de la publicación a editar.
     * @param publicationDTO  Nuevos detalles de la publicación.
     * @return ResponseEntity con PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Edit a publication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Publication edited successfully",
                content = @Content(schema = @Schema(implementation = PublicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Publication not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/user/{userId}/publication/{publicationId}")
    public ResponseEntity<PublicationDTO> editPublication(@PathVariable(name = "userId") Long userId,
                                                         @PathVariable(name = "publicationId") Long publicationId,
                                                         @RequestBody PublicationDTO publicationDTO) {
        try {
            PublicationDTO editedPublication = publicationService.editPublication(userId, publicationId, publicationDTO);
            return ResponseEntity.ok(editedPublication);
        } catch (Exception e) {
            log.error("Error during editPublication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una publicación existente.
     *
     * @param userId          Identificador único del usuario que realizó la publicación.
     * @param publicationId   Identificador único de la publicación a eliminar.
     * @return ResponseEntity con el estado HTTP correspondiente.
     */
    @Operation(summary = "Delete a publication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Publication deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Publication not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/user/{userId}/publication/{publicationId}")
    public ResponseEntity<Void> deletePublication(@PathVariable(name = "userId") Long userId,
                                                  @PathVariable(name = "publicationId") Long publicationId) {
        try {
            publicationService.deletePublication(userId, publicationId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error during deletePublication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una publicación por su identificador único.
     *
     * @param publicationId Identificador único de la publicación a recuperar.
     * @return ResponseEntity con PublicationDTO y el estado HTTP correspondiente.
     */
    @Operation(summary = "Get publication by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved publication by ID",
                content = @Content(schema = @Schema(implementation = PublicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Publication not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/publication/{publicationId}")
    public ResponseEntity<PublicationDTO> getPublicationById(@PathVariable("publicationId") Long publicationId) {
        try {
            PublicationDTO publication = publicationService.getPublicationByPublicationId(publicationId);
            if (publication != null) {
                return ResponseEntity.ok(publication);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            log.error("Error during getPublicationById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Edita los detalles de un usuario.
     *
     * @param userId       Identificador único del usuario a editar.
     * @param userDetails  Nuevos detalles del usuario.
     * @return ResponseEntity con el estado HTTP correspondiente.
     */
    @Operation(summary = "Edit user details")
    @ApiResponse(responseCode = "200", description = "User details edited successfully",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Transactional
    @PutMapping("/{userId}/edit/details")
    public ResponseEntity<Map<String, String>> editUserDetails(
            @PathVariable(name = "userId") Long userId,
            @Parameter(description = "New user details", content = @Content(schema = @Schema(implementation = String.class)))
            @RequestBody Map<String, String> userDetails) {
        System.out.println("Received Request - User ID: " + userId + ", New Details: " + userDetails);
        try {
            Map<String, String> updatedDetails = userService.editUserDetails(
                    userId,
                    userDetails.get("newDescription"),
                    userDetails.get("newEmail"));
            return ResponseEntity.ok(updatedDetails);
        } catch (DataIntegrityViolationException e) {
            log.error("Error during editUserDetails: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error during editUserDetails: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene todos los comentarios asociados a una publicación.
     *
     * @param publicationId Identificador de la publicación.
     * @return ResponseEntity con la lista de comentarios.
     */
    @Operation(summary = "Get comments by publication ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Publication not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/publication/{publicationId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPublicationId(@PathVariable(name = "publicationId") Long publicationId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByPublicationId(publicationId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error getting comments for publication ID " + publicationId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Agrega un comentario a una publicación.
     *
     * @param publicationId Identificador de la publicación.
     * @param userId        Identificador del usuario que realiza el comentario.
     * @param commentDTO    DTO que contiene la información del comentario.
     * @return ResponseEntity con el comentario agregado.
     */
    @Operation(summary = "Add a comment to a publication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Publication or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/publication/{publicationId}/comments/user/{userId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable(name = "publicationId") Long publicationId,
                                                 @PathVariable(name = "userId") Long userId,
                                                 @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO addedComment = commentService.addComment(userId, publicationId, commentDTO);
            return ResponseEntity.ok(addedComment);
        } catch (Exception e) {
            log.error("Error adding comment to publication ID " + publicationId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}