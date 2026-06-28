package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultResponse {
    private UUID          id;
    private UUID          quizId;
    private String        quizTitle;
    private UUID          studentId;
    private String        studentName;
    private int           score;
    private boolean       isPassed;
    private int           attemptNumber;
    private LocalDateTime takenAt;
}
