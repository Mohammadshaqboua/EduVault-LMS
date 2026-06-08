package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class QuizSubmitRequest {
    @NotNull
    private UUID quizId;

    @NotEmpty
    private Map<UUID, Integer> answers;
}