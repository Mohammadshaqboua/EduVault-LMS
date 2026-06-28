package com.example.eduvaultlms.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalStudents;
    private long totalCourses;
    private long publishedCourses;
    private long totalEnrollments;
    private long totalCertificatesIssued;
    private long totalQuizzes;
    private double averageCompletionRate;
}