package com.example.lms_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateDto {

    Long id;

    @NotBlank(message = "Name cannot be empty!")
    String name;

    @NotBlank(message = "Description cannot be empty!")
    String description;

    @NotBlank(message = "Content cannot be empty!")
    String content;

    @NotBlank(message = "Order cannot be empty!")
    Integer order;
}
