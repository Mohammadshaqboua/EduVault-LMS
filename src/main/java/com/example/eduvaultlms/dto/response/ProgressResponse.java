package com.example.eduvaultlms.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ProgressResponse {
    private UUID courseId;
    private String courseTitle;
    private int totalLessons;
    private int completedLessons;
    private double progressPercentage;
    private double completionPct;
}
