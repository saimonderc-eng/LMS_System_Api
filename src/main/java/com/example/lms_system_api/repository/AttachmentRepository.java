package com.example.lms_system_api.repository;

import com.example.lms_system_api.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Boolean existsByUrl(String url);
}
