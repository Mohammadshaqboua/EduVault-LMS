package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Lesson;
import com.example.eduvaultlms.model.LessonProgress;
import com.example.eduvaultlms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    Optional<LessonProgress> findByStudentIdAndLessonId(User student, Lesson lesson);
    int countByStudentIdAndIsCompletedTrue(User student);
    List<LessonProgress> findByStudentId(User student);
    List<LessonProgress> findByStudentIdAndLessonId_Course_Id(User student, UUID courseId);
    long countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(User student, UUID courseId);

}