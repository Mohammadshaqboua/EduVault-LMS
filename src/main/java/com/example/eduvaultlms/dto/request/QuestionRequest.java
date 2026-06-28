package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionRequest {

    @NotBlank(message = "text is required")
    private String text;

    @NotEmpty(message = "options must not be empty")
    @Size(min = 2, message = "A question must have at least 2 options")
    private List<String> options;

    @NotNull(message = "correctIndex is required")
    @Min(value = 0, message = "correctIndex must be >= 0")
    private Integer correctIndex;

    @NotNull(message = "points is required")
    @Min(value = 1, message = "points must be at least 1")
    private Integer points;
}