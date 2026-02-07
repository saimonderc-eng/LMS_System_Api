package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.CreateUserRequest;
import com.example.lms_system_api.dto.UpdatePasswordRequest;
import com.example.lms_system_api.dto.UpdateUserRequest;
import com.example.lms_system_api.dto.UserDto;
import com.example.lms_system_api.exception.*;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;
    private final AuthService authService;

    @Value("${keycloak.admin.realm}")
    private String realm;

    public void createUser(CreateUserRequest request) {
        log.info("Requested to create user by username: {}", request.getUsername());
        try {
            UsersResource users = keycloak.realm(realm).users();

            CredentialRepresentation temporaryPassword = new CredentialRepresentation();
            temporaryPassword.setType(CredentialRepresentation.PASSWORD);
            temporaryPassword.setValue("temp12345");
            temporaryPassword.setTemporary(true);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setCredentials(List.of(temporaryPassword));
            user.setEnabled(true);

            log.debug("User values: {}", user);
            try (Response response = users.create(user)) {
                int status = response.getStatus();

                if (status == 201) {
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    log.info("User created successfully with id: {}", userId);

                    RoleRepresentation adminRole = keycloak.realm(realm)
                            .roles().get(request.getRole()).toRepresentation();

                    users.get(userId).roles().realmLevel().add(List.of(adminRole));
                    log.info("Role: {} successfully assigned to user: {}", request.getRole(), request.getUsername());
                } else if (status == 409){
                    log.warn("User with username: {} already exists in keycloak!", request.getUsername());
                    throw new LmsUsernameAlreadyExistsException("Username already taken!");
                } else {
                    log.error("Failed to create user, keycloak status: {}", status);
                    throw new LmsBadRequestException("Could not create user!");
                }
            }
        } catch (HttpServerErrorException ex) {
            log.error("Keycloak error during creating user: {}, {}!", request.getUsername(), ex.getMessage());
            throw new LmsAuthException("Couldn't reach out keycloak server!");
        } catch (Exception ex) {
            log.error("Unexpected error occurred creating user!");
            throw new LmsInternalException("Internal creating error!");
        }
    }

    public UserDto getCurrentUser() {
        log.info("Requested to get current user");
        try {
            return Optional.of(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .map(auth -> auth.getPrincipal())
                    .map(principal -> {
                        if (principal instanceof UserDto userDto) {
                            return userDto;
                        } else if (principal instanceof Jwt jwt) {
                            return new UserDto(
                                    jwt.getClaimAsString("sub"),
                                    jwt.getClaimAsString("preferred_username"),
                                    jwt.getClaimAsString("email"),
                                    jwt.getClaimAsString("given_name"),
                                    jwt.getClaimAsString("family_name")
                            );
                        } else {
                            throw new LmsNotFoundException("User not found!");
                        }
                    })
                    .orElseThrow(() -> new LmsNotFoundException("User not found!"));
        } catch (HttpServerErrorException ex) {
            log.error("Keycloak error during getting current user!");
            throw new LmsAuthException("Couldn't reach out keycloak!");
        } catch (Exception ex) {
            log.error("Unexpected error occurred trying to get current user!");
            throw new LmsInternalException("Internal error!");
        }

    }

    public void update(UpdateUserRequest request) {
        log.info("Requested to update user values!");
        try {
            UserDto currentUser = getCurrentUser();

            if (keycloak.realm(realm).users().search(request.getUsername()).stream()
                    .anyMatch(u -> !u.getId().equals(request.getId())
                            && u.getUsername().equals(request.getUsername()))) {
                log.warn("Username: {} already taken!", request.getUsername());
                throw new LmsUsernameAlreadyExistsException("Username already taken!");
            }

            safetySaveValue(request.getUsername(), currentUser::setUsername);
            safetySaveValue(request.getEmail(), currentUser::setEmail);

            UserRepresentation user = keycloak.realm(realm).users().get(currentUser.getId()).toRepresentation();
            user.setUsername(currentUser.getUsername());
            user.setEmail(currentUser.getEmail());

            keycloak.realm(realm).users().get(currentUser.getId()).update(user);
            log.info("Successfully updated user by id: {} values!", request.getId());
        } catch (HttpServerErrorException ex) {
            log.error("Keycloak error trying to update valuer for user id: {}, {}!", request.getId(), ex.getMessage());
            throw new LmsAuthException("Couldn't reach out keycloak!");
        } catch (Exception ex) {
            log.error("Unexpected error occurred trying to update user values!");
            throw new LmsInternalException("Internal updating error!");
        }
    }

    public void updatePassword(UpdatePasswordRequest request) {
        log.info("Requested to update user password by id: {}", request.getId());
        try {
            UserDto currentUser = getCurrentUser();

            if (!currentUser.getId().equals(request.getId())) {
                log.warn("Error trying to change foreign password!");
                throw new ForbiddenException("You can change only your password!");
            }
            boolean oldPasswordValid = authService.validatePassword(currentUser.getUsername(), request.getCurrentPassword());
            if (!oldPasswordValid) {
                log.warn("Incorrect credentials, try again!");
                throw new LmsIncorrectPasswordException("Current password is incorrect!");
            }
            CredentialRepresentation newPassword = new CredentialRepresentation();
            newPassword.setType(CredentialRepresentation.PASSWORD);
            newPassword.setValue(request.getNewPassword());
            newPassword.setTemporary(false);

            if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
                log.warn("Passwords must be the same, try again!");
                throw new LmsBadRequestException("Passwords are not the same!");
            }
            keycloak.realm(realm)
                    .users().get(currentUser.getId()).resetPassword(newPassword);
            log.info("Successfully change password for user by id: {}!", request.getId());
        } catch (WebApplicationException ex) {
            log.error("Keycloak API error during password updating: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error occurred trying to update password!");
            throw new LmsInternalException("Internal password updating error!");

        }
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}
