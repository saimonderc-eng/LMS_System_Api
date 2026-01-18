package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.entity.Lesson;
import com.example.lms_system_api.mapper.LessonMapper;
import com.example.lms_system_api.repository.LessonRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public List<LessonDto> getLessons() {
        List<Lesson> lessons = lessonRepository.findAll();

        return lessonMapper.toDtoList(lessons);
    }

    public LessonDto createLesson(LessonDto dto) {
        Lesson lesson = lessonMapper.toEntity(dto);
        Lesson addedLesson = lessonRepository.save(lesson);

        return lessonMapper.toDto(addedLesson);
    }

    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));
        lessonRepository.delete(lesson);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public LessonDto updateLesson(@Valid LessonUpdateDto dto) {
        Lesson lesson = lessonRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Lessons not found!"));
        if (dto.getName() != null && !dto.getName().equals(lesson.getName()) && lessonRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Lessons with this username already exists!");
        }
        safetySaveValue(dto.getName(), lesson::setName);
        safetySaveValue(dto.getDescription(), lesson::setDescription);
        safetySaveValue(dto.getContent(), lesson::setContent);
        safetySaveValue(dto.getOrder(), lesson::setOrder);

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }
}
