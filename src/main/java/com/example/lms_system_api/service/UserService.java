package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.CreateUserRequest;
import com.example.lms_system_api.dto.UpdatePasswordRequest;
import com.example.lms_system_api.dto.UpdateUserRequest;
import com.example.lms_system_api.dto.UserDto;
import com.example.lms_system_api.exception.BadRequestEx;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.exception.UsernameAlreadyExistsException;
import jakarta.ws.rs.ForbiddenException;
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


        try (Response response = users.create(user)) {
            int status = response.getStatus();

            if (status == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                log.info("User created successfully with id: {}", userId);

                RoleRepresentation adminRole = keycloak.realm(realm)
                        .roles().get(request.getRole()).toRepresentation();

                users.get(userId).roles().realmLevel().add(List.of(adminRole));
                log.info("Role: {} successfully assigned to user: {}", request.getRole(), request.getUsername());
            } else {
                log.error("Failed to create user status: {}", status);
            }
        }
    }

    public UserDto getCurrentUser() {
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
                        throw new NotFoundException("User not found!");
                    }
                })
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public void update(UpdateUserRequest request) {
        UserDto currentUser = getCurrentUser();

        if (keycloak.realm(realm).users().search(request.getUsername()).stream()
                .anyMatch(u -> !u.getId().equals(request.getId())
                        && u.getUsername().equals(request.getUsername()))) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        safetySaveValue(request.getUsername(), currentUser::setUsername);
        safetySaveValue(request.getEmail(), currentUser::setEmail);

        UserRepresentation user = keycloak.realm(realm).users().get(currentUser.getId()).toRepresentation();
        user.setUsername(currentUser.getUsername());
        user.setEmail(currentUser.getEmail());

        keycloak.realm(realm).users().get(currentUser.getId()).update(user);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public void updatePassword(UpdatePasswordRequest request) {
        UserDto currentUser = getCurrentUser();

        if(!currentUser.getId().equals(request.getId())){
            throw new ForbiddenException("You can change only your password!");
        }
        boolean oldPasswordValid = authService.validatePassword(currentUser.getUsername(), request.getCurrentPassword());
        if(!oldPasswordValid){
            throw new BadRequestEx("Current password is incorrect!");
        }
        CredentialRepresentation newPassword = new CredentialRepresentation();
        newPassword.setType(CredentialRepresentation.PASSWORD);
        newPassword.setValue(request.getNewPassword());
        newPassword.setTemporary(false);

        if(!request.getNewPassword().equals(request.getNewPasswordConfirmation())){
            throw new BadRequestEx("Passwords are not the same!");
        }
        keycloak.realm(realm)
                .users().get(currentUser.getId()).resetPassword(newPassword);
    }
}
