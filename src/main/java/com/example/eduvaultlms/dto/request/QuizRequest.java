package com.example.eduvaultlms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizRequest {
    @NotNull(message = "courseId is required")
    private UUID courseId;

    @NotBlank(message = "title is required")
    @Size(max = 200, message = "title must not exceed 200 characters")
    private String title;

    @NotNull(message = "passMark is required")
    @Min(value = 0,   message = "passMark must be between 0 and 100")
    @Max(value = 100, message = "passMark must be between 0 and 100")
    private Integer passMark;

    @NotNull(message = "timeLimitMin is required")
    @Min(value = 1, message = "timeLimitMin must be at least 1 minute")
    private Integer timeLimitMin;

    @NotEmpty(message = "A quiz must have at least one question")
    @Valid
    private List<QuestionRequest> questions;}
