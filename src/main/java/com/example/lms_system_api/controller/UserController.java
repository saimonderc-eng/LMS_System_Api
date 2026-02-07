package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.UpdatePasswordRequest;
import com.example.lms_system_api.dto.UpdateUserRequest;
import com.example.lms_system_api.dto.UserDto;
import com.example.lms_system_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Пользовательский контроллер",
        description = "Контроллер позволяющий пользователям менять свой аккаунт" +
                " или просматривать свою страницу.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Страница пользователя",
            description = "Endpoint для просмотра своих данных(кроме пароля и ролей)." +
                    " Требует действительного access-токена.",
            tags = "user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен действителен, страница пользователя предоставлена!"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден!")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> homePage() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Изменение пользовательских данных",
            description = "Изменение данных пользователем кроме пароля. " +
                    "Требует действительного access-токена. Отправляет 200 в случае успеха" +
                    " и 409 если имя занято.",
            tags = "user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены!"),
            @ApiResponse(responseCode = "409", description = "Имя пользователя занято!")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        userService.update(request);
        return ResponseEntity.ok("User details updated!");
    }

    @Operation(summary = "Обновление пароля",
            description = "Обновление пароля пользователем. Требует действительного access-токена," +
                    " текущего пароля, новый пароль и его подтверждение." +
                    " Отправляет 200 в случае успешного сохранения пароля и 400/403 если произошла ошибка.",
            tags = "user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль успешно обновлен!"),
            @ApiResponse(responseCode = "403", description = "Вы можете обновлять только свой пароль!"),
            @ApiResponse(responseCode = "422", description = "Неправильный текущий пароль!"),
            @ApiResponse(responseCode = "400", description = "Пароли не совпадают!")

    })
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok("Password successfully updated!");
    }
}
