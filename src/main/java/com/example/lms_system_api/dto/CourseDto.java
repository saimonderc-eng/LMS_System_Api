package com.example.lms_system_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDto {

    Long id;

    @NotBlank(message = "Name is required!")
    String name;

    @NotBlank(message = "Description is required!")
    String description;
}
