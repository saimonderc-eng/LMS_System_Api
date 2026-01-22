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
public class ChapterUpdateDto {

    @Schema(description = "Уникальный идентификатор",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    @Schema(description = "Обновленное название главы",
            example = "Java Basics Demo",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank!")
    String name;

    @Schema(description = "Обновленное описание главы",
            example = "В Java Basics Demo вы получите базовые" +
                    " знания о джаве")
    @NotBlank(message = "Description cannot be blank!")
    String description;

    @NotBlank(message = "Order cannot be blank!")
    Integer order;
}
