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
public class CourseDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Название курса",
            example = "Java Junior Developer",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required!")
    String name;

    @Schema(description = "Описание курса", example = "В Java Junior Developer курсе вы получите знания достаточные для работы")
    @NotBlank(message = "Description is required!")
    String description;
}
