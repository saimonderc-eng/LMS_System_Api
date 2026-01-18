package com.example.lms_system_api.repository;

import com.example.lms_system_api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findById(Long id);

    Boolean existsByName(String name);

    Long id(Long id);
}
