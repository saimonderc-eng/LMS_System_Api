package com.example.lms_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateDto {

    Long id;

    @NotEmpty(message = "Name cannot be empty")
    String name;

    @NotEmpty(message = "Description cannot be empty")
    String description;
}
