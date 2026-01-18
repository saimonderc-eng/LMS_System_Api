package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseDto> getCourses() {
        return courseService.getCourses();
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> updateCourse(@Valid @RequestBody CourseUpdateDto dto){
        CourseDto updatedCourse = courseService.updateCourse(dto);
        return ResponseEntity.ok(updatedCourse);
    }
}
