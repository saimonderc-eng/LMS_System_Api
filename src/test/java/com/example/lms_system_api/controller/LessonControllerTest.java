package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest(LessonController.class)
public class LessonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LessonService lessonService;


    @Test
    void getLessons_Success() throws Exception {
        Mockito.when(lessonService.getLessons()).thenReturn(List.of(new LessonDto()));

        mockMvc.perform(get("/api/v1/lessons"))
                .andExpect(status().isOk());
    }

    @Test
    void createLesson_Success() throws Exception {
        LessonDto dto = new LessonDto();
        dto.setName("Basics");
        dto.setChapterId(1L);

        Mockito.when(lessonService.createLesson(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateLesson_Success() throws Exception {
        LessonUpdateDto updateDto = new LessonUpdateDto();
        updateDto.setName("New Lesson Name");

        mockMvc.perform(put("/api/v1/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLesson_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/lessons/1"))
                .andExpect(status().isNoContent());
    }
}
