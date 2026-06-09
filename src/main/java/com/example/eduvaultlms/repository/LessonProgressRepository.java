package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    Optional<LessonProgress> findByStudentIdAndLessonId(UUID studentId, UUID lessonId);
    int countByStudentIdAndIsCompletedTrue(UUID studentId);
    List<LessonProgress> findByStudentId(UUID studentId);
}