package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.mapper.CourseMapper;
import com.example.lms_system_api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;


    public List<CourseDto> getCourses() {
        log.info("Requested to get all courses");

        List<Course> courses = courseRepository.findAll();
        log.debug("Found: {} courses in database", courses.size());

        return courseMapper.toDtoList(courses);
    }

    public CourseDto createCourse(CourseDto dto) throws BadRequestException {
        log.info("Started creating course with name: {}", dto.getName());

        if (courseRepository.existsByName(dto.getName())) {
            log.error("Course with name: {} already exists", dto.getName());
            throw new BadRequestException("Course with this name already exists!");
        }
        Course course = courseMapper.toEntity(dto);
        Course newCourse = courseRepository.save(course);

        log.info("Successfully created course with id: {}", newCourse.getId());
        return courseMapper.toDto(newCourse);
    }

    public void deleteCourse(Long id) {
        log.info("Requested to delete course by id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course by id: {} not found", id);
                    return new NotFoundException("Course not found");
                });
        courseRepository.delete(course);
        log.info("Successfully deleted course by id: {}", id);
    }


    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public CourseDto updateCourse(Long id, CourseUpdateDto dto) throws BadRequestException {
        log.info("Requested to update course: {}", dto.getName());

        log.debug("Course details: {}", dto);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course by id: {} not found!", id);
                    return new NotFoundException("Course not found!");
                });
        if (dto.getName() != null && !dto.getName().equals(course.getName()) && courseRepository.existsByName(dto.getName())) {
            log.error("Course with name: {} already exists in database!", dto.getName());
            throw new BadRequestException("Course with this name already exists!");
        }
        safetySaveValue(dto.getName(), course::setName);
        safetySaveValue(dto.getDescription(), course::setDescription);

        courseRepository.save(course);
        log.info("Successfully saved values for: {}", id);
        return courseMapper.toDto(course);
    }
}
