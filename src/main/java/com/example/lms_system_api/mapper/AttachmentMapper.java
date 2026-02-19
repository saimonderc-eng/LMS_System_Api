package com.example.lms_system_api.mapper;

import com.example.lms_system_api.dto.AttachmentDto;
import com.example.lms_system_api.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(source = "lesson.id", target = "lessonId")
    AttachmentDto toDto(Attachment entity);

    @Mapping(source = "lessonId", target = "lesson.id")
    Attachment toEntity(AttachmentDto dto);
}
