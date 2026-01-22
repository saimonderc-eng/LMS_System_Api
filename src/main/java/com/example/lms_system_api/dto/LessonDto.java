package com.example.lms_system_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Название урока",
            example = "Массивы",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Name is required")
    String name;

    @Schema(description = "Описание урока",
            example = "На этом уроке вы научитесь пользоватся массивами")
    @NotEmpty(message = "Description is required")
    String description;

    @Schema(description = "Содержание урока",
            example = "Массивы - это .....")
    @NotEmpty(message = "Content is required")
    String content;

    @Schema(description = "Порядковый номер, для сортировки внутри главы",
            example = "1")
    int order;

    @Schema(description = "Уникальный идентификатор главы",
            example = "2")
    Long chapterId;
}
