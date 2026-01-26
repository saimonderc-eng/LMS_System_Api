package com.example.lms_system_api.serivce;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.BadRequestEx;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.mapper.ChapterMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import com.example.lms_system_api.repository.CourseRepository;
import com.example.lms_system_api.service.ChapterService;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChapterServiceTest {
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private ChapterService chapterService;

    @Test
    void getAllChapters_success() {
        List<Chapter> entities = List.of(new Chapter(), new Chapter());
        List<ChapterDto> dtos = List.of(new ChapterDto(), new ChapterDto());

        when(chapterRepository.findAll()).thenReturn(entities);
        when(chapterMapper.toDtoList(entities)).thenReturn(dtos);

        List<ChapterDto> result = chapterService.getChapters();

        assertEquals(2, result.size());
        verify(chapterRepository).findAll();
    }
    @Test
    void getChapterById_success() {
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        ChapterDto dto = new ChapterDto();
        dto.setId(1L);
        Long testId = 1L;

        when(chapterRepository.findById(testId)).thenReturn(Optional.of(chapter));
        when(chapterMapper.toDto(chapter)).thenReturn(dto);

        ChapterDto result = chapterService.findChapterById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(chapterRepository).findById(testId);
    }

    @Test
    @DisplayName("Ошибка 404 если глава не найдена")
    void getChapterById_NotFound_Exception(){
        Chapter chapter = new Chapter();
        chapter.setId(1L);

        when(chapterRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> chapterService.findChapterById(2L));
        verify(chapterRepository, never()).save(any());

    }

    @Test
    void createChapter_Success() {
        ChapterDto dto = new ChapterDto();
        dto.setId(1L);
        dto.setCourseId(1L);
        dto.setName("Basics");
        Chapter chapter = new Chapter();
        Course course = new Course();

        when(chapterRepository.existsByName("Basics")).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(chapterMapper.toEntity(dto)).thenReturn(chapter);
        when(chapterRepository.save(any())).thenReturn(chapter);
        when(chapterMapper.toDto(chapter)).thenReturn(dto);

        ChapterDto result = chapterService.createChapter(dto);

        assertNotNull(result);
        verify(chapterRepository).save(any());
    }
    @Test
    @DisplayName("Ошибка 404 если id курса не найден")
    void createChapter_CourseNotFound() {
        ChapterDto dto = new ChapterDto();
        dto.setCourseId(100L);
        dto.setName("Basics Demo");

        when(courseRepository.existsById(100L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                chapterService.createChapter(dto));

        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка 400 если имя главы уже занято")
    void createChapter_NameExists_Exception() {
        ChapterDto dto = new ChapterDto();
        dto.setName("Basics");

        when(courseRepository.existsById(any())).thenReturn(true);
        when(chapterRepository.existsByName("Basics")).thenReturn(true);

        assertThrows(BadRequestEx.class, () -> chapterService.createChapter(dto));
        verify(chapterRepository, never()).save(any());
    }

    @Test
    void deleteChapter_Success(){
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));

        chapterService.deleteChapter(1L);

        verify(chapterRepository).delete(chapter);
    }

    @Test
    @DisplayName("Ошибка 404 если id главы не найден")
    void deleteChapter_NotFound_Exception(){
        Long id = 1L;
        when(chapterRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> chapterService.deleteChapter(id));
        verify(chapterRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateChapter_Success(){
        Long id = 1L;
        ChapterUpdateDto updateDto = new ChapterUpdateDto();
        updateDto.setDescription("Java Basics");
        Chapter existingChapter = new Chapter();

        when(chapterRepository.findById(id)).thenReturn(Optional.of(existingChapter));
        when(chapterMapper.toDto(existingChapter)).thenReturn(new ChapterDto());

        chapterService.updateChapter(id, updateDto);
        verify(chapterRepository).save(existingChapter);
    }

    @Test
    @DisplayName("Ошибка 404 если главы не существует")
    void updateChapter_NotFound_Exception(){
        Long id = 1L;
        ChapterUpdateDto dto = new ChapterUpdateDto();
        dto.setName("New Name");

        when(chapterRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> chapterService.updateChapter(id, dto));
        verify(chapterRepository, never()).save(any());
    }
    @Test
    @DisplayName("Ошибка 400 если имя главы занято")
    void updateChapter_BadRequest_Exception(){
        Long id = 1L;
        ChapterUpdateDto dto = new ChapterUpdateDto();
        dto.setName("New Chapter");
        Chapter Chapter = new Chapter();
        Chapter.setId(id);

        when(chapterRepository.findById(id)).thenReturn(Optional.of(Chapter));
        when(chapterRepository.existsByName("New Chapter")).thenReturn(true);

        assertThrows(BadRequestEx.class, () ->
                chapterService.updateChapter(id, dto));
        verify(chapterRepository, never()).save(any());
    }
}
