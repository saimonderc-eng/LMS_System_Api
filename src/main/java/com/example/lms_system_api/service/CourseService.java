package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.mapper.CourseMapper;
import com.example.lms_system_api.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;


    public List<CourseDto> getCourses() {
        List<Course> courses = courseRepository.findAll();

        return courseMapper.toDtoList(courses);
    }
    public CourseDto createCourse(CourseDto dto) {
        Course course = courseMapper.toEntity(dto);
        Course newCourse = courseRepository.save(course);

        return courseMapper.toDto(newCourse);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }


    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
    public CourseDto updateCourse(@Valid CourseUpdateDto dto) {
        Course course = courseRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        if(dto.getName() != null && !dto.getName().equals(course.getName()) && courseRepository.existsByName(dto.getName())){
            throw new RuntimeException("Course with this name already exists!");
        }
        safetySaveValue(dto.getName(), course::setName);
        safetySaveValue(dto.getDescription(), course::setDescription);

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }
}
