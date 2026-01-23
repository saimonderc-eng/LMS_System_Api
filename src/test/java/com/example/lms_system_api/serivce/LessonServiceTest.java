package com.example.lms_system_api.serivce;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.entity.Lesson;
import com.example.lms_system_api.exception.BadRequestEx;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.mapper.LessonMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import com.example.lms_system_api.repository.LessonRepository;
import com.example.lms_system_api.service.LessonService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LessonServiceTest {
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonMapper lessonMapper;
    @Mock
    private ChapterRepository chapterRepository;
    @InjectMocks
    private LessonService lessonService;

    @Test
    void getAllLessons_success() {
        List<Lesson> entities = List.of(new Lesson(), new Lesson());
        List<LessonDto> dtos = List.of(new LessonDto(), new LessonDto());

        when(lessonRepository.findAll()).thenReturn(entities);
        when(lessonMapper.toDtoList(entities)).thenReturn(dtos);

        List<LessonDto> result = lessonService.getLessons();

        assertEquals(2, result.size());
        verify(lessonRepository).findAll();
    }

    @Test
    void createLesson_Success() {
        LessonDto dto = new LessonDto();
        dto.setId(1L);
        dto.setChapterId(1L);
        dto.setName("Basics");
        Lesson lesson = new Lesson();
        Chapter chapter = new Chapter();

        when(lessonRepository.existsByName("Basics")).thenReturn(false);
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(lessonMapper.toEntity(dto)).thenReturn(lesson);
        when(lessonRepository.save(any())).thenReturn(lesson);
        when(lessonMapper.toDto(lesson)).thenReturn(dto);

        LessonDto result = lessonService.createLesson(dto);

        assertNotNull(result);
        verify(lessonRepository).save(any());
    }
    @Test
    @DisplayName("Ошибка 404 если id главы не найден")
    void createLesson_CourseNotFound() {
        LessonDto dto = new LessonDto();
        dto.setChapterId(100L);
        dto.setName("Basics Demo");

        when(chapterRepository.existsById(100L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                lessonService.createLesson(dto));

        verify(chapterRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка 400 если имя урока уже занято")
    void createLessons_NameExists_Exception() {
        LessonDto dto = new LessonDto();
        dto.setName("Basics");
        when(lessonRepository.existsById(any())).thenReturn(true);
        when(lessonRepository.existsByName("Basics")).thenReturn(true);

        assertThrows(BadRequestEx.class, () -> lessonService.createLesson(dto));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void deleteLesson_Success(){
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.deleteLesson(1L);
        verify(lessonRepository).delete(lesson);
    }

    @Test
    @DisplayName("Ошибка 404 если id урока не найден")
    void deleteLesson_NotFound_Exception(){
        Long id = 1L;
        when(lessonRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> lessonService.deleteLesson(id));
        verify(lessonRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateLesson_Success(){
        Long id = 1L;
        LessonUpdateDto updateDto = new LessonUpdateDto();
        updateDto.setDescription("Java Basics");
        Lesson existingLesson = new Lesson();

        when(lessonRepository.findById(id)).thenReturn(Optional.of(existingLesson));
        when(lessonMapper.toDto(existingLesson)).thenReturn(new LessonDto());

        lessonService.updateLesson(id, updateDto);
        verify(lessonRepository).save(existingLesson);
    }

    @Test
    @DisplayName("Ошибка 404 если урока не существует")
    void updateLesson_NotFound_Exception(){
        Long id = 1L;
        LessonUpdateDto dto = new LessonUpdateDto();
        dto.setName("New Lesson");

        when(lessonRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> lessonService.updateLesson(id, dto));
        verify(lessonRepository, never()).save(any());
    }
    @Test
    @DisplayName("Ошибка 400 если имя урока занято")
    void updateLesson_BadRequest_Exception(){
        Long id = 1L;
        LessonUpdateDto dto = new LessonUpdateDto();
        dto.setName("New Lesson");
        Lesson lesson = new Lesson();
        lesson.setId(id);

        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));
        when(lessonRepository.existsByName("New Lesson")).thenReturn(true);

        assertThrows(BadRequestEx.class, () ->
                lessonService.updateLesson(id, dto));
        verify(lessonRepository, never()).save(any());
    }
}

