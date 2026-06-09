package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByIsPublishedTrue();
    List<Course> findByCreatedById(UUID userId);
}
