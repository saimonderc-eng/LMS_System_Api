package com.example.lms_system_api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentDto {

    Long id;
    String name;
    String url;
    Long lessonId;
}
