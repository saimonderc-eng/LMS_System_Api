package com.example.lms_system_api.service;

import com.example.lms_system_api.dto.AttachmentDto;
import com.example.lms_system_api.entity.Attachment;
import com.example.lms_system_api.entity.Lesson;
import com.example.lms_system_api.exception.LmsNotFoundException;
import com.example.lms_system_api.mapper.AttachmentMapper;
import com.example.lms_system_api.repository.AttachmentRepository;
import com.example.lms_system_api.repository.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final FileService fileService;
    private final LessonRepository lessonRepository;

    @Transactional
    public AttachmentDto upload(MultipartFile file, Long lessonId) {
        log.info("STARTED processing attachment for lesson ID: {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.error("ERROR occurred, lesson by ID: {} does not exists", lessonId);
                    return new LmsNotFoundException("Lesson not found!");
                });
        String fileKey = fileService.uploadFile(file);

        Attachment attachment = Attachment.builder()
                .name(file.getOriginalFilename())
                .url(fileKey)
                .lesson(lesson)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        log.info("SUCCESSFULLY saved attachment by key: {}!", fileKey);

        return attachmentMapper.toDto(savedAttachment);
    }

    @Transactional
    public byte[] download(String url) {
        log.info("REQUESTED download by url: {}", url);

        if (!attachmentRepository.existsByUrl(url)) {
            log.error("ERROR file by url: {} not found!", url);
            throw new LmsNotFoundException("File not found!");
        }
        log.info("SUCCESSFULLY found file by url: {} download starting", url);
        return fileService.downloadFile(url);
    }
}
