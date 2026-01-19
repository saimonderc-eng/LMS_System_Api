package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.entity.Course;
import com.example.lms_system_api.exception.NotFoundException;
import com.example.lms_system_api.mapper.ChapterMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import com.example.lms_system_api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
        log.info("Requested to get all chapters");

        List<Chapter> chapters = chapterRepository.findAll();
        log.debug("Found: {} chapters in database", chapters.size());

        return chapterMapper.toDtoList(chapters);
    }


    public ChapterDto createChapter(ChapterDto dto) throws BadRequestException {
        log.info("Requested to create chapter: '{}' for course id: {}", dto.getName(), dto.getCourseId());

        log.debug("Chapter details: {}", dto);
        if (chapterRepository.existsByName(dto.getName())) {
            log.error("Chapter with name: {} already exists!", dto.getName());
            throw new BadRequestException("Chapter with this name already exists!");
        }
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> {
                    log.error("Course by id: {} not found", dto.getCourseId());
                    return new NotFoundException("Course not found!");
                });
        Chapter chapter = chapterMapper.toEntity(dto);
        chapter.setCourse(course);
        Chapter newChapter = chapterRepository.save(chapter);

        log.info("Successfully created chapter with id: {}", newChapter.getId());
        return chapterMapper.toDto(newChapter);
    }

    public void deleteChapter(Long id) {
        log.info("Requested to delete chapter by id: {}", id);
        Chapter chapter = chapterRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Chapter with id: {} not found!", id);
                    return new NotFoundException("Chapter not found!");
                });
        chapterRepository.delete(chapter);
        log.info("Successfully deleted chapter by id: {}", id);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public ChapterDto updateChapter(Long id, ChapterUpdateDto dto) throws BadRequestException {
        log.info("Requested to update chapter by id: {}", id);

        log.debug("Chapter details: {}", dto);
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Chapter by id: {} not found!", id);
                    return new NotFoundException("Chapter not found!");
                });
        if (dto.getName() != null && !dto.getName().equals(chapter.getName()) && chapterRepository.existsByName(dto.getName())) {
            log.error("Chapter with name: {} already exists in database!", dto.getName());
            throw new BadRequestException("Chapter with this name already exists!");
        }
        safetySaveValue(dto.getName(), chapter::setName);
        safetySaveValue(dto.getDescription(), chapter::setDescription);
        safetySaveValue(dto.getOrder(), chapter::setOrder);

        chapterRepository.save(chapter);
        log.info("Successfully saved values for: {}", id);
        return chapterMapper.toDto(chapter);
    }
}
