package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.request.CourseRequest;
import com.example.eduvaultlms.dto.request.LessonRequest;
import com.example.eduvaultlms.dto.request.ReorderRequest;
import com.example.eduvaultlms.dto.response.CourseResponse;
import com.example.eduvaultlms.dto.response.LessonResponse;
import com.example.eduvaultlms.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private Validator validator;

    @GetMapping("/api/courses/{courseId}/lessons")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PostMapping(value = "/{courseId}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable UUID courseId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "video", required = true) MultipartFile video) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        LessonRequest request = mapper.readValue(requestJson, LessonRequest.class);

        LessonResponse created = lessonService.createLesson(courseId, request, video);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable UUID id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        LessonRequest request = objectMapper.readValue(requestJson, LessonRequest.class);

        Set<ConstraintViolation<LessonRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        LessonResponse updated = lessonService.updateLesson(id, request, thumbnail);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> reorderLesson(
            @PathVariable UUID id,
            @Valid @RequestBody ReorderRequest request) {

        LessonResponse updated = lessonService.reorderLesson(id, request.newOrderIndex());
        return ResponseEntity.ok(updated);
    }
}