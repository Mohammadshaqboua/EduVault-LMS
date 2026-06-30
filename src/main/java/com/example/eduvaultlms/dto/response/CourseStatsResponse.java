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
public class CourseStatsResponse {

    private UUID    courseId;
    private String courseTitle;
    private long    totalEnrollments;
    private double  averageCompletionPct;
    private long    completedStudents;
    private long    totalLessons;
    private long    totalQuizzes;
}