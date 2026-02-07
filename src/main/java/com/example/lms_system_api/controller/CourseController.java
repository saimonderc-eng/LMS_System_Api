package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Курсы",
        description = "Методы для работы с курсами обучения")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Получить список всех курсов",
            description = "Выводит список всех курсов")
    @ApiResponse(responseCode = "200", description = "Список всех курсов успешно получен!")
    @GetMapping
    public List<CourseDto> getCourses() {
        return courseService.getCourses();
    }

    @Operation(summary = "Создать новый курс",
            description = "Регистрация нового курса в систему. Требует уникального названия. " +
                    "Иначе возвращает ошибку 400 Bad Request",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Курс успешно создан!"),
            @ApiResponse(responseCode = "400", description = "Курс с таким названием уже существует!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @Operation(summary = "Удалить курс по уникальному идентификатору",
            description = "Помечает курс как удаленный(soft delete). " +
                    "Если курс по id не найден выводит 404 Not Found",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Курс успешно удален!"),
            @ApiResponse(responseCode = "404", description = "Курс по указанному id не найден!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить значение курса по уникальному идентификатору",
            description = "Обновляет указанные значения курса по его id. Требует существующий id курса и уникальное новое название. " +
                    "Иначе выводит ошибку 404 Not Found/ 400 Bad Request",
            tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Указанные значения курса успешно обновлены!"),
            @ApiResponse(responseCode = "404", description = "Курс по указанному id не найден!"),
            @ApiResponse(responseCode = "400", description = "Курс с таким названием уже существует!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseUpdateDto dto) {
        CourseDto updatedCourse = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(updatedCourse);
    }

    @Operation(summary = "Найти курс по уникальному идентификатору",
            description = "Отправляет курс найденный по её id. Требует id существующего курса. "
                    + "Иначе выводит ошибку 404 Not Found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курс успешно найден!"),
            @ApiResponse(responseCode = "404", description = "Курс по указанному id не найден!")
    })
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<CourseDto> findCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findCourseById(id));
    }
}
