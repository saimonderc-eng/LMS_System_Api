package com.example.lms_system_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        log.info("STARTED uploading file with: original name: {}, size: {}",
                file.getOriginalFilename(), file.getSize());

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("FILE with key: {} uploaded successfully!", filename);
            return filename;

        } catch (IOException e) {
            log.error("ERROR occurred reading file for: {}", file.getOriginalFilename());
            throw new RuntimeException("Error occurred trying to upload file!", e);
        } catch (Exception e) {
            log.error("UNIDENTIFIED MinIO occurred uploading file!");
            throw new RuntimeException("Error occurred with MinIO!", e);
        }
    }

    public byte[] downloadFile(String filename) {
        log.info("STARTED downloading file: {}", filename);

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            byte[] content = response.readAllBytes();

            log.info("SUCCESSFULLY downloaded file: {}, size: {}", filename, content.length);
            return content;

        } catch (IOException e) {
            log.error("IO error occurred reading file from MinIO: {}", filename, e);
            throw new RuntimeException("Couldn't read file from MinIO!", e);

        } catch (Exception e) {
            log.error("COULDN'T find file: {}, from bucket: {}", filename, bucket);
            throw new RuntimeException("Error occurred finding file!", e);
        }
    }
}
