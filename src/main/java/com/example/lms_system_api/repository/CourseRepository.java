package com.example.lms_system_api.repository;

import com.example.lms_system_api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Boolean existsByName(String name);
}
