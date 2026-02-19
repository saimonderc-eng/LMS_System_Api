package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.LmsBadRequestException;
import com.example.lms_system_api.exception.LmsNotFoundException;
import com.example.lms_system_api.mapper.CourseMapper;
import com.example.lms_system_api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("REQUESTED to get all courses");

        List<Course> courses = courseRepository.findAll();
        log.debug("FOUND: {} courses in database", courses.size());

        return courseMapper.toDtoList(courses);
    }

    public CourseDto createCourse(CourseDto dto) {
        log.info("STARTED creating course with name: {}", dto.getName());

        if (courseRepository.existsByName(dto.getName())) {
            log.error("COURSE with name: {} already exists", dto.getName());
            throw new LmsBadRequestException("Course with this name already exists!");
        }
        Course course = courseMapper.toEntity(dto);
        Course newCourse = courseRepository.save(course);

        log.info("SUCCESSFULLY created course with id: {}", newCourse.getId());
        return courseMapper.toDto(newCourse);
    }

    public void deleteCourse(Long id) {
        log.info("REQUESTED to delete course by id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("COURSE by id: {} not found", id);
                    return new LmsNotFoundException("Course not found");
                });
        courseRepository.delete(course);
        log.info("SUCCESSFULLY deleted course by id: {}", id);
    }


    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public CourseDto updateCourse(Long id, CourseUpdateDto dto) {
        log.info("REQUESTED to update course: {}", dto.getName());

        log.debug("COURSE details: {}", dto);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("COURSE by id: {} not found!", id);
                    return new LmsNotFoundException("Course not found!");
                });
        if (dto.getName() != null && !dto.getName().equals(course.getName()) && courseRepository.existsByName(dto.getName())) {
            log.error("COURSE with name: {} already exists in database!", dto.getName());
            throw new LmsBadRequestException("Course with this name already exists!");
        }
        safetySaveValue(dto.getName(), course::setName);
        safetySaveValue(dto.getDescription(), course::setDescription);

        courseRepository.save(course);
        log.info("SUCCESSFULLY saved values for: {}", id);
        return courseMapper.toDto(course);
    }

    public CourseDto findCourseById(Long id) {
        log.info("REQUESTED to find course by id: {}", id);

        Course foundCourse = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("COURSE by id: {} not found!", id);
                    return new LmsNotFoundException("Course not found!");
                });
        log.debug("FOUND course details: {}", foundCourse);

        log.info("SUCCESSFULLY found course by id: {} !", id);
        return courseMapper.toDto(foundCourse);
    }
}
