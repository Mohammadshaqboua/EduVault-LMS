package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Enrollment;
import com.example.eduvaultlms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentIdAndCourseId(User student, Course course);
    Optional<Enrollment> findByStudentIdAndCourseId(User student, Course course);
    List<Enrollment> findByStudentId(User student);
    List<Enrollment> findByCourseId(Course course);
    long countByCourseId(UUID courseId);
}