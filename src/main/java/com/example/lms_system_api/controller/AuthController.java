package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LoginRequestDto;
import com.example.lms_system_api.dto.RefreshTokenRequest;
import com.example.lms_system_api.dto.TokenResponse;
import com.example.lms_system_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication",
        description = "Методы авторизации пользователей и " +
                "обновления JWT токенов.")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Авторизация пользователя",
            description = "Принимает логин и пароль, возвращает jwt access & refresh токены. " +
                    "Отправляет ошибку 409.",
            tags = "auth"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Логин прошел успешно!"),
            @ApiResponse(responseCode = "409", description = "Keycloak токен пуст!")
    })
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @Operation(
            summary = "Обновление токенов",
            description = "Запрос требует существующего refresh токена. " +
                    "Отправляет ошибку 409 если refresh токен пуст.",
            tags = "auth"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены!"),
            @ApiResponse(responseCode = "409", description = "Refresh токен пуст!")
    })
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }
}
