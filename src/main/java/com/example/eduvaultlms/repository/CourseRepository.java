package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByIsPublishedTrue();
    List<Course> findByCreatedById(UUID userId);
    long countByIsPublishedTrue();

    // Most-enrolled published course
    @Query("""
            SELECT c FROM Course c
            JOIN Enrollment e ON e.id = c.id
            WHERE c.isPublished = true
            GROUP BY c.id
            ORDER BY COUNT(e.id) DESC
            LIMIT 1
            """)
    Course findMostEnrolledCourse();

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId")
    long countLessonsByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.id = :courseId")
    long countQuizzesByCourseId(@Param("courseId") UUID courseId);
}
