package com.example.eduvaultlms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizSubmitRequest {
    @NotEmpty(message = "answers must not be empty")
    @Valid
    private List<AnswerRequest> answers;

    public List<AnswerRequest> getAnswers()             { return answers; }
    public void setAnswers(List<AnswerRequest> answers) { this.answers = answers; }
}