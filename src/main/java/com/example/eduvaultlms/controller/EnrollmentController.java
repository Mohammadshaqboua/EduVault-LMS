package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.response.EnrollmentResponse;
import com.example.eduvaultlms.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable UUID courseId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(courseId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> unenroll(@PathVariable UUID courseId) {
        enrollmentService.unenroll(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(id));
    }
}