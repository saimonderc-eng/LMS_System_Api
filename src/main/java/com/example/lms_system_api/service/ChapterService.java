package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.LmsBadRequestException;
import com.example.lms_system_api.exception.LmsNotFoundException;
import com.example.lms_system_api.mapper.ChapterMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import com.example.lms_system_api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final ChapterMapper chapterMapper;

    public List<ChapterDto> getChapters() {
        log.info("REQUESTED to get all chapters");

        List<Chapter> chapters = chapterRepository.findAll();
        log.debug("FOUND: {} chapters in database", chapters.size());

        return chapterMapper.toDtoList(chapters);
    }


    public ChapterDto createChapter(ChapterDto dto) {
        log.info("REQUESTED to create chapter: '{}' for course id: {}", dto.getName(), dto.getCourseId());

        log.debug("CHAPTER details: {}", dto);
        if (chapterRepository.existsByName(dto.getName())) {
            log.error("CHAPTER with name: {} already exists!", dto.getName());
            throw new LmsBadRequestException("Chapter with this name already exists!");
        }
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> {
                    log.error("COURSE by id: {} not found", dto.getCourseId());
                    return new LmsNotFoundException("Course not found!");
                });
        Chapter chapter = chapterMapper.toEntity(dto);
        chapter.setCourse(course);
        Chapter newChapter = chapterRepository.save(chapter);

        log.info("SUCCESSFULLY created chapter with id: {}", newChapter.getId());
        return chapterMapper.toDto(newChapter);
    }

    public void deleteChapter(Long id) {
        log.info("REQUESTED to delete chapter by id: {}", id);
        Chapter chapter = chapterRepository.findById(id).
                orElseThrow(() -> {
                    log.error("CHAPTER with id: {} not found!", id);
                    return new LmsNotFoundException("Chapter not found!");
                });
        chapterRepository.delete(chapter);
        log.info("SUCCESSFULLY deleted chapter by id: {}", id);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public ChapterDto updateChapter(Long id, ChapterUpdateDto dto) {
        log.info("REQUESTED to update chapter by id: {}", id);

        log.debug("CHAPTER details: {}", dto);
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CHAPTER by id: {} not found!", id);
                    return new LmsNotFoundException("Chapter not found!");
                });
        if (dto.getName() != null && !dto.getName().equals(chapter.getName()) && chapterRepository.existsByName(dto.getName())) {
            log.error("CHAPTER with name: {} already exists in database!", dto.getName());
            throw new LmsBadRequestException("Chapter with this name already exists!");
        }
        safetySaveValue(dto.getName(), chapter::setName);
        safetySaveValue(dto.getDescription(), chapter::setDescription);
        safetySaveValue(dto.getOrder(), chapter::setOrder);

        chapterRepository.save(chapter);
        log.info("SUCCESSFULLY saved values for: {}", id);
        return chapterMapper.toDto(chapter);
    }

    public ChapterDto findChapterById(Long id) {
        log.info("REQUESTED to find chapter by id: {}", id);

        Chapter foundChapter = chapterRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CHAPTER by id: {} not found!", id);
                    return new LmsNotFoundException("Chapter not found!");
                });
        log.debug("FOUND chapter details: {}", foundChapter);

        log.info("SUCCESSFULLY found chapter by id: {} !", id);
        return chapterMapper.toDto(foundChapter);
    }
}
