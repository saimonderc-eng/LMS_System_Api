package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LessonDto;
import com.example.lms_system_api.dto.LessonUpdateDto;
import com.example.lms_system_api.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class LessonControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();
    }

    @Test
    void getLessons_Success() throws Exception {
        Mockito.when(lessonService.getLessons()).thenReturn(List.of(new LessonDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/lessons"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createLesson_Success() throws Exception {
        LessonDto dto = new LessonDto();
        dto.setName("Basics");
        dto.setChapterId(1L);

        Mockito.when(lessonService.createLesson(any())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateLesson_Success() throws Exception {
        LessonUpdateDto updateDto = new LessonUpdateDto();
        updateDto.setName("New Lesson Name");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteLesson_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/lessons/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
