package com.example.lms_system_api.mapper;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.entity.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    List<CourseDto> toDtoList(List<Course> entities);

    CourseDto toDto(Course entity);

    Course toEntity(CourseDto dto);
}
