package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.CourseDto;
import com.example.lms_system_api.dto.CourseUpdateDto;
import com.example.lms_system_api.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest(CourseController.class)
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseService courseService;


    @Test
    void getAll_Success() throws Exception {
        when(courseService.getCourses()).thenReturn(List.of(new CourseDto()));

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void create_Success() throws Exception {
        CourseDto dto = new CourseDto();
        dto.setName("Java");

        when(courseService.createCourse(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void update_Success() throws Exception {
        CourseUpdateDto updateDto = new CourseUpdateDto();
        updateDto.setName("New Name");

        mockMvc.perform(put("/api/v1/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/1"))
                .andExpect(status().isNoContent());
    }
}

