package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Уроки",
        description = "Методы для работы с уроками обучения")
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "Получить список всех уроков",
            description = "Выводит список всех уроков")
    @ApiResponse(responseCode = "200", description = "Список всех уроков успешно получен!")
    @GetMapping
    public List<LessonDto> getLessons() {
        return lessonService.getLessons();
    }

    @Operation(summary = "Создать новый урок",
            description = "Регистрация нового урока в систему. Требует уникального названия и id существующей главы. " +
                    "Иначе возвращает ошибку 400 Bad Request/ 404 Not Found",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Урок успешно создан!"),
            @ApiResponse(responseCode = "400", description = "Урок с таким названием уже существует!"),
            @ApiResponse(responseCode = "404", description = "Указанная глава (chapter_id) не найдена!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createLessons(@RequestBody LessonDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(dto));
    }

    @Operation(summary = "Удалить урок по уникальному идентификатору",
            description = "Помечает урок как удаленный(soft delete). " +
                    "Если урок по id не найден выводит 404 Not Found",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Урок успешно удален!"),
            @ApiResponse(responseCode = "404", description = "Урок по указанному id не найден!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить значения урока по уникальному идентификатору",
            description = "Обновляет указанные значения урока по его id. Требует существующий id урока и уникальное новое название. " +
                    "Иначе выводит ошибку 404 Not Found/ 400 Bad Request",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Указанные значения урока успешно обновлены!"),
            @ApiResponse(responseCode = "404", description = "Урок по указанному id не найден!"),
            @ApiResponse(responseCode = "400", description = "Урок с таким названием уже существует!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLessons(@PathVariable Long id, @RequestBody LessonUpdateDto dto) {
        LessonDto updatedLesson = lessonService.updateLesson(id, dto);
        return ResponseEntity.ok(updatedLesson);
    }

    @Operation(summary = "Найти урок по уникальному идентификатору",
            description = "Отправляет урок найденный по его id. Требует id существующего урока. " +
                    "Иначе отправляет ошибку 404 Not Found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Урок успешно найден!"),
            @ApiResponse(responseCode = "404", description = "Урок по указанному id не найден!")
    })
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<LessonDto> findLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findLessonById(id));
    }
}
