package com.example.lms_system_api.serivce;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.entity.Chapter;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
    void createChapter_Success() {
        ChapterDto dto = new ChapterDto();
        dto.setName("Basics");
        Chapter chapter = new Chapter();

        when(courseRepository.existsById(any())).thenReturn(true);
        when(chapterRepository.existsByName(any())).thenReturn(false);
        when(chapterRepository.existsByName("Basics")).thenReturn(false);
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
        Long id = 1L;
        when(chapterRepository.existsById(anyLong())).thenReturn(true);
        when(chapterRepository.existsById(id)).thenReturn(true);

        chapterService.deleteChapter(id);
        verify(chapterRepository).deleteById(id);
    }

    @Test
    @DisplayName("Ошибка 404 если id главы не найден")
    void deleteChapter_NotFound_Exception(){
        Long id = 1L;
        when(chapterRepository.existsById(id)).thenReturn(false);

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
