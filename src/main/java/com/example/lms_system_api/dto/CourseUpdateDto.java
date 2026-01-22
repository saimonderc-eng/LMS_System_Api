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
public class CourseUpdateDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Обновленное название курса",
            example = "Java Middle Developer",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank")
    String name;

    @Schema(description = "Обновленное описание курса",
            example = "В Java Middle Developer курсе вы получите знания" +
                    " достаточные для работы на Middle позиции")
    @NotBlank(message = "Description cannot be blank")
    String description;
}
