package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.entity.Lesson;
import com.example.lms_system_api.exception.LmsBadRequestException;
import com.example.lms_system_api.exception.LmsNotFoundException;
import com.example.lms_system_api.mapper.LessonMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import com.example.lms_system_api.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {

    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public List<LessonDto> getLessons() {
        log.info("Requested to get all lessons");

        List<Lesson> lessons = lessonRepository.findAll();
        log.debug("Found: {} lessons in database", lessons.size());

        return lessonMapper.toDtoList(lessons);
    }

    public LessonDto createLesson(LessonDto dto) {
        log.info("Requested to create lesson: '{}' for chapter id: {}", dto.getName(), dto.getChapterId());

        log.debug("Lesson details: {}", dto);
        if (lessonRepository.existsByName(dto.getName())) {
            log.error("Lesson with name: {} already exists in database!", dto.getName());
            throw new LmsBadRequestException("Lesson with this name already exists!");
        }
        Chapter chapter = chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> {
                    log.error("Failed to create lesson: Chapter with id: {} not found!", dto.getChapterId());
                    return new LmsNotFoundException("Chapter not found!");
                });
        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setChapter(chapter);

        Lesson addedLesson = lessonRepository.save(lesson);

        log.info("Lesson successfully created with id: {}", addedLesson.getId());
        return lessonMapper.toDto(addedLesson);
    }

    public void deleteLesson(Long id) {
        log.info("Requested to delete lesson by id: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lesson by id: {} not found", id);
                    return new LmsNotFoundException("Lesson not found!");
                });
        lessonRepository.delete(lesson);
        log.info("Successfully deleted lesson by id: {}", id);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public LessonDto updateLesson(Long id, LessonUpdateDto dto) {
        log.info("Requested to update values for: {}", dto.getName());

        log.debug("Lesson details: {}", dto);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lesson by id: {} not found", id);
                    return new LmsNotFoundException("Lesson not found!");
                });
        if (dto.getName() != null && !dto.getName().equals(lesson.getName()) && lessonRepository.existsByName(dto.getName())) {
            log.error("Lesson with name: {} already exists in database!", dto.getName());
            throw new LmsBadRequestException("Lessons with this name already exists!");
        }
        safetySaveValue(dto.getName(), lesson::setName);
        safetySaveValue(dto.getDescription(), lesson::setDescription);
        safetySaveValue(dto.getContent(), lesson::setContent);
        safetySaveValue(dto.getOrder(), lesson::setOrder);

        lessonRepository.save(lesson);
        log.info("Successfully saved values for: {}", id);
        return lessonMapper.toDto(lesson);
    }

    public LessonDto findLessonById(Long id) {
        log.info("Requested to find lesson by id: {}", id);

        Lesson foundLesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lesson by id: {} not found!", id);
                    return new LmsNotFoundException("Lesson not found!");
                });
        log.debug("Lesson details: {}", foundLesson);

        log.info("Successfully found lesson by id: {} !", id);
        return lessonMapper.toDto(foundLesson);
    }
}
