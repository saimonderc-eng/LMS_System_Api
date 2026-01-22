package com.example.lms_system_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Обновленное название урока",
            example = "Циклы",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank!")
    String name;

    @Schema(description = "Обновленное описание урока",
            example = "На этом уроке вы научитесь пользоватся циклами")
    @NotBlank(message = "Description cannot be blank!")
    String description;

    @Schema(description = "Обновленное содержание урока",
            example = "Циклы - это .....")
    @NotBlank(message = "Content cannot be blank!")
    String content;

    @Schema(description = "Обновленный порядок урока",
            example = "2")
    @NotBlank(message = "Order cannot be blank!")
    Integer order;
}
