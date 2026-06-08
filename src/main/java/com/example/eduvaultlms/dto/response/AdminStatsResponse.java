package com.example.eduvaultlms.dto.response;

import lombok.Data;

@Data
public class AdminStatsResponse {
    private long totalUsers;
    private long totalCourses;
    private long totalLessons;
    private long totalEnrollments;
}
