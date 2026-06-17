package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.EnrollmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {
    public EnrollmentResponse enroll(UUID courseId) {
    }

    public List<EnrollmentResponse> getMyEnrollments() {
    }

    public void unenroll(UUID courseId) {
    }

    public List<EnrollmentResponse> getEnrollmentsByCourse(UUID id) {
    }
}
