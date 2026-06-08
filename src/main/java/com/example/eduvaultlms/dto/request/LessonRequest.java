package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class LessonRequest {
    @NotBlank @Size(max = 200)
    private String title;

    private String videoUrl;

    private String content;

    private Integer duration;

    @NotNull @Min(1)
    private Integer orderIndex;

    @NotNull
    private UUID courseId;
}