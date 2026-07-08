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
    long countByIsPublishedTrue();

    @Query("SELECT c FROM Course c JOIN Enrollment e ON e.course.id = c.id WHERE e.status = 'COMPLETED' GROUP BY c ORDER BY COUNT(e) DESC")
    Course findMostEnrolledCourse();

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId")
    long countLessonsByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.id = :courseId")
    long countQuizzesByCourseId(@Param("courseId") UUID courseId);
}
