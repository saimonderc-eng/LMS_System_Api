package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping
    public List<ChapterDto> getChapters(){
        return chapterService.getChapters();
    }

    @PostMapping
    public ResponseEntity<?> createChapter(@RequestBody ChapterDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(chapterService.createChapter(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id){
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping
    public ResponseEntity<?> updateChapter(@Valid @RequestBody ChapterUpdateDto dto){
        ChapterDto updatedChapter = chapterService.updateChapter(dto);
        return ResponseEntity.ok(updatedChapter);
    }
}
