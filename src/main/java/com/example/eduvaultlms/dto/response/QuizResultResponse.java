package com.example.eduvaultlms.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuizResultResponse {
    private UUID quizId;
    private String quizTitle;
    private int score;
    private Boolean isPassed;
    private int attemptNumber;
    private LocalDateTime takenAt;
}
