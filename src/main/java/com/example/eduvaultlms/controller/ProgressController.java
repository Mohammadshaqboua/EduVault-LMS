package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.response.CourseProgressResponse;
import com.example.eduvaultlms.dto.response.LessonProgressResponse;
import com.example.eduvaultlms.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/lessons/{id}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonProgressResponse> completeLesson(@PathVariable("id") UUID lessonId) {
        return ResponseEntity.ok(progressService.completeLesson(lessonId));
    }

    @GetMapping("/courses/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(@PathVariable("id") UUID courseId) {
        return ResponseEntity.ok(progressService.getCourseProgress(courseId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseProgressResponse>> getMyProgress() {
        return ResponseEntity.ok(progressService.getMyProgress());
    }
}