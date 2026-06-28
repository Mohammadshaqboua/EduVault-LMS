package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, UUID> {
    int countByStudentIdAndQuizId(UUID studentId, UUID quizId);

    List<QuizResult> findByStudentIdAndQuizIdOrderByAttemptNumberAsc(UUID studentId, UUID quizId);

    List<QuizResult> findByQuizIdOrderByStudentIdAscAttemptNumberAsc(UUID quizId);
}
