package com.example.lms_system_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateDto {

    Long id;

    @NotEmpty(message = "Name cannot be empty!")
    String name;

    @NotEmpty(message = "Description cannot be empty!")
    String description;

    @NotEmpty(message = "Content cannot be empty!")
    String content;

    @NotEmpty(message = "Order cannot be empty!")
    int order;
}
