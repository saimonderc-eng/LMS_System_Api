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
public class ChapterDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Название главы",
            example = "Java Basics",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Name is required!")
    String name;

    @Schema(description = "Описание главы",
            example = "В этой главе вы ознакомитесь с базой джавы")
    @NotEmpty(message = "Description is required!")
    String description;

    @Schema(description = "Порядковый номер, для сортировки внутри главы",
            example = "1")
    int order;

    @Schema(description = "Уникальный идентификатор курса",
            example = "2")
    Long courseId;
}
