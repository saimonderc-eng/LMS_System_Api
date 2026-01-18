package com.example.lms_system_api.repository;

import com.example.lms_system_api.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    Boolean existsByName(String name);
}
