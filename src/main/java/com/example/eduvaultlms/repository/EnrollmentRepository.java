package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Enrollment;
import com.example.eduvaultlms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
    long countByCourseId(UUID courseId);
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    @Query("SELECT AVG(e.completionPct) FROM Enrollment e WHERE e.id = :courseId")
    Double avgCompletionPctByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.id = :courseId AND e.completionPct = 100.0")
    long countCompletedStudentsByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT AVG(e.completionPct) FROM Enrollment e")
    Double avgCompletionPctPlatform();
}