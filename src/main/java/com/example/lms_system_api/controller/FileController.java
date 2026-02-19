package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.AttachmentDto;
import com.example.lms_system_api.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Файлы",
        description = "Методы для работы с файлами")
public class FileController {

    private final AttachmentService attachmentService;

    @Operation(summary = "Загрузить файл",
            description = "Загружает файл в MinIO, требует сам файл и id существуюшего урока. " +
                    "Доступен только админу или учителю. " +
                    "Иначе возвращает ошибки 404 Not Found /403 Forbidden.",
            tags = {
                    "admin",
                    "MinIO"
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл загружен успешно!"),
            @ApiResponse(responseCode = "404", description = "Ошибка, урок по указанному id не найден!"),
            @ApiResponse(responseCode = "403", description = "У вас нет прав доступа!")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("lessonId") Long lessonId
    ) {
        log.info("REST started uploading file: {}, for lesson: {}", file.getOriginalFilename(), lessonId);
        return ResponseEntity.ok(attachmentService.upload(file, lessonId));
    }

    @Operation(summary = "Скачать файл",
            description = "Скачивает файл по указанному пользователем url. " +
                    "Доступен всем аутентифицированным пользователям. " +
                    "В случае ошибки возвращает 404 Not Found.",
            tags = "MinIo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Загрузка файла успешно начата!"),
            @ApiResponse(responseCode = "404", description = "Файл по указанному url не найден!"),
    })
    @GetMapping("/download/{url}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> download(@PathVariable String url) {
        log.info("REST started downloading file with url: {}", url);
        byte[] data = attachmentService.download(url);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + url + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
