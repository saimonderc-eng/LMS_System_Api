package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.CreateUserRequest;
import com.example.lms_system_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Админский контроллер",
        description = "Админский контроллер для работы с пользователями.")
public class AdminUserController {

    private final UserService userService;

    @Operation(
            summary = "Регистрация пользоватя",
            description = "Endpoint для регистрации пользователя админом. " +
                    "Для создания пользователя используются параметры из CreateUserRequest." +
                    "Выбрасывает 201 в случае успеха и 400 в случае ошибки.",
            tags = "admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан!"),
            @ApiResponse(responseCode = "400", description = "Ошибка при создании пользователя!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public void register(@RequestBody CreateUserRequest request) {
        userService.createUser(request);
    }
}
