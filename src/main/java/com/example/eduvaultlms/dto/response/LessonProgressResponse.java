package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressResponse {
    private UUID lessonId;
    private String lessonTitle;
    private Boolean isCompleted;
    private LocalDateTime watchedAt;
    private Integer watchedSeconds;
}