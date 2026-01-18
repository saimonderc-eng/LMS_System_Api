package com.example.lms_system_api.mapper;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    List<LessonDto> toDtoList(List<Lesson> entities);

    @Mapping(source = "chapter.id", target = "chapterId")
    LessonDto toDto(Lesson entity);

    @Mapping(source = "chapterId", target = "chapter.id")
    Lesson toEntity(LessonDto dto);
}
