package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.request.LessonRequest;
import com.example.eduvaultlms.dto.response.LessonResponse;
import com.example.eduvaultlms.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/api/courses/{courseId}/lessons")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PostMapping("/api/courses/{courseId}/lessons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(@PathVariable UUID courseId,
                                                       @Valid @RequestBody LessonRequest request ,
                                                        MultipartFile video) {
        LessonResponse created = lessonService.createLesson(courseId, request , video);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable UUID id,
                                                       @Valid @RequestBody LessonRequest request,
                                                       MultipartFile video) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, video));
    }

    @DeleteMapping("/api/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/lessons/{id}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> reorderLesson(@PathVariable UUID id,
                                                        @RequestBody Map<String, Integer> body) {
        int newOrderIndex = body.get("orderIndex");
        return ResponseEntity.ok(lessonService.reorderLesson(id, newOrderIndex));
    }
}