package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Главы",
        description = "Методы для работы с главами обучения")
public class ChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "Получить список всех глав",
            description = "Выводит список всех глав")
    @ApiResponse(responseCode = "200", description = "Список всех глав успешно получен!")
    @GetMapping
    public List<ChapterDto> getChapters() {
        return chapterService.getChapters();
    }

    @Operation(summary = "Создать новый урок",
            description = "Регистрация новой главы в систему. Требует уникального названия и id существующего курса. " +
                    "Иначе возвращает ошибку 400 Bad Request/ 404 Not Found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Глава успешно создана!"),
            @ApiResponse(responseCode = "400", description = "Глава с таким названием уже существует!"),
            @ApiResponse(responseCode = "404", description = "Указанный курс (courseId) не найден!")
    })
    @PostMapping
    public ResponseEntity<?> createChapter(@RequestBody ChapterDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(chapterService.createChapter(dto));
    }

    @Operation(summary = "Удалить главу",
            description = "Помечает главу как удаленную(soft delete). " +
                    "Если глава по id не найдена выводит 404 Not Found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Глава успешно удалена!"),
            @ApiResponse(responseCode = "404", description = "Глава по указанному id не найдена!")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить значения главы",
            description = "Обновляет указанные значения главы по её id. Требует существующий id главы и уникальное новое название. " +
                    "Иначе выводит ошибку 404 Not Found/ 400 Bad Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Указанные значения главы успешно обновлены!"),
            @ApiResponse(responseCode = "404", description = "Глава по указанному id не найдена!"),
            @ApiResponse(responseCode = "400", description = "Глава с таким названием уже существует!")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @RequestBody ChapterUpdateDto dto){
        ChapterDto updatedChapter = chapterService.updateChapter(id, dto);
        return ResponseEntity.ok(updatedChapter);
    }
}
