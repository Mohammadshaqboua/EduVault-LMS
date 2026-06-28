package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AnswerRequest {

    @NotNull(message = "questionId is required")
    private UUID questionId;

    @NotNull(message = "selectedIndex is required")
    @Min(value = 0, message = "selectedIndex must be >= 0")
    private Integer selectedIndex;

}