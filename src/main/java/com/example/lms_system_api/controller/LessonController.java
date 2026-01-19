package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public List<LessonDto> getLessons() {
        return lessonService.getLessons();
    }

    @PostMapping
    public ResponseEntity<?> createLessons(@RequestBody LessonDto dto) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLessons(@PathVariable Long id, @RequestBody LessonUpdateDto dto) throws BadRequestException {
        LessonDto updatedLesson = lessonService.updateLesson(id, dto);
        return ResponseEntity.ok(updatedLesson);
    }
}
