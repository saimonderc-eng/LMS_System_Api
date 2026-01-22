package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.ChapterDto;
import com.example.lms_system_api.dto.ChapterUpdateDto;
import com.example.lms_system_api.service.ChapterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChapterControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterController chapterController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chapterController).build();
    }

    @Test
    void getChapters_Success() throws Exception {
        when(chapterService.getChapters()).thenReturn(List.of(new ChapterDto()));

        mockMvc.perform(get("/api/v1/chapters"))
                .andExpect(status().isOk());
    }

    @Test
    void createChapter_Success() throws Exception {
        ChapterDto dto = new ChapterDto();
        dto.setName("Basics");
        dto.setCourseId(1L);

        when(chapterService.createChapter(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/chapters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateChapter_Success() throws Exception {
        ChapterUpdateDto updateDto = new ChapterUpdateDto();
        updateDto.setName("New Chapter Name");

        mockMvc.perform(put("/api/v1/chapters/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteChapter_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/chapters/1"))
                .andExpect(status().isNoContent()); // или .isOk() в зависимости от твоего кода
    }
}