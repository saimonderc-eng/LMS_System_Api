package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.entity.Chapter;
import com.example.lms_system_api.mapper.ChapterMapper;
import com.example.lms_system_api.repository.ChapterRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    public List<ChapterDto> getChapters() {
        List<Chapter> chapters = chapterRepository.findAll();

        return chapterMapper.toDtoList(chapters);
    }


    public ChapterDto createChapter(ChapterDto dto) {
        Chapter chapter = chapterMapper.toEntity(dto);
        Chapter newChapter = chapterRepository.save(chapter);

        return chapterMapper.toDto(newChapter);
    }

    public void deleteChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Chapter not found!"));
        chapterRepository.delete(chapter);
    }

    public <T> void safetySaveValue(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public ChapterDto updateChapter(@Valid ChapterDto dto) {
        Chapter chapter = chapterRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Chapter not found!"));
        if(dto.getName() != null && !dto.getName().equals(chapter.getName()) && chapterRepository.existsByName(dto.getName())){
            throw new RuntimeException("Chapter with this name already exists!");
        }
        safetySaveValue(dto.getName(), chapter::setName);
        safetySaveValue(dto.getDescription(), chapter::setDescription);
        safetySaveValue(dto.getOrder(), chapter::setOrder);

        chapterRepository.save(chapter);
        return chapterMapper.toDto(chapter);
    }
}
