package com.example.lms_system_api.serivce;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.BadRequestEx;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.mapper.CourseMapper;
import com.example.lms_system_api.repository.CourseRepository;
import com.example.lms_system_api.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseService courseService;

    @Test
    void getAllCourses_success() {
        List<Course> entities = List.of(new Course(), new Course());
        List<CourseDto> dtos = List.of(new CourseDto(), new CourseDto());

        when(courseRepository.findAll()).thenReturn(entities);
        when(courseMapper.toDtoList(entities)).thenReturn(dtos);

        List<CourseDto> result = courseService.getCourses();

        assertEquals(2, result.size());
        verify(courseRepository).findAll();
    }

    @Test
    void findCourseById_success() {
        Course course = new Course();
        course.setId(1L);
        CourseDto dto = new CourseDto();
        dto.setId(1L);
        Long testId = 1L;

        when(courseRepository.findById(testId)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(dto);

        CourseDto result = courseService.findCourseById(testId);
        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(courseRepository).findById(testId);
    }

    @Test
    @DisplayName("Ошибка 404 если курс не найден")
    void findCourseById_NotFound_Exception() {
        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courseService.findCourseById(2L));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void createCourse_Success() {
        CourseDto dto = new CourseDto();
        dto.setName("Java");
        Course course = new Course();

        when(courseRepository.existsByName("Java")).thenReturn(false);
        when(courseMapper.toEntity(any(CourseDto.class))).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toDto(course)).thenReturn(dto);

        CourseDto result = courseService.createCourse(dto);

        assertNotNull(result);
        verify(courseRepository).save(any());
    }

    @Test
    @DisplayName("Ошибка 400 если имя курса уже занято")
    void createCourse_NameExists_Exception() {
        CourseDto dto = new CourseDto();
        dto.setName("Java");
        when(courseRepository.existsByName("Java")).thenReturn(true);

        assertThrows(BadRequestEx.class, () -> courseService.createCourse(dto));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void deleteCourse_Success() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }

    @Test
    @DisplayName("Ошибка 404 если id курса не найден")
    void deleteCourse_NotFound_Exception() {
        Long id = 1L;
        when(courseRepository.existsById(anyLong())).thenReturn(false);
        when(courseRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> courseService.deleteCourse(id));
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateCourse_Success() {
        Long id = 1L;
        CourseUpdateDto updateDto = new CourseUpdateDto();
        updateDto.setDescription("Python");
        Course existingCourse = new Course();

        when(courseRepository.findById(id)).thenReturn(Optional.of(existingCourse));
        when(courseMapper.toDto(existingCourse)).thenReturn(new CourseDto());

        courseService.updateCourse(id, updateDto);
        verify(courseRepository).save(existingCourse);
    }

    @Test
    @DisplayName("Ошибка 404 если курса не существует")
    void updateCourse_NotFound_Exception() {
        Long id = 1L;
        CourseUpdateDto dto = new CourseUpdateDto();
        dto.setName("New Name");

        when(courseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> courseService.updateCourse(id, dto));
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка 400 если имя курса занято")
    void updateCourse_BadRequest_Exception() {
        Long id = 1L;
        CourseUpdateDto dto = new CourseUpdateDto();
        dto.setName("New Course");
        Course course = new Course();
        course.setId(id);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(courseRepository.existsByName("New Course")).thenReturn(true);

        assertThrows(BadRequestEx.class, () ->
                courseService.updateCourse(id, dto));
        verify(courseRepository, never()).save(any());
    }
}
