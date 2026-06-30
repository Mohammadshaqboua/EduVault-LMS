package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressResponse {
    private UUID    courseId;
    private String  courseTitle;
    private Integer completedLessons;
    private Integer totalLessons;
    private Double  completionPct;
    private String  enrollmentStatus;
}