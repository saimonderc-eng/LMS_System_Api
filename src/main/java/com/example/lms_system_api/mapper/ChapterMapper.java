package com.example.lms_system_api.mapper;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.entity.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    List<ChapterDto> toDtoList(List<Chapter> entities);

    @Mapping(source = "course.id", target = "courseId")
    ChapterDto toDto(Chapter entity);

    @Mapping(source = "courseId", target = "course.id")
    Chapter toEntity(ChapterDto dto);
}
