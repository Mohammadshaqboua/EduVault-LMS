package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User studentId;

    @ManyToOne
    @JoinColumn(name = "quizId", nullable = false)
    private Quiz quizId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Boolean isPassed = false;

    @Column(nullable = false)
    private Integer attemptNumber = 1;

    @Column(updatable = false)
    private LocalDateTime takenAt = LocalDateTime.now();
}