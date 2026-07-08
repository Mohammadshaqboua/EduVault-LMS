package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.request.CourseRequest;
import com.example.eduvaultlms.dto.response.CourseResponse;
import com.example.eduvaultlms.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
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
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private jakarta.validation.Validator validator;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(){
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID id){
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        CourseRequest request = objectMapper.readValue(requestJson, CourseRequest.class);

        CourseResponse created = courseService.createCourse(request, thumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable UUID id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        CourseRequest request = objectMapper.readValue(requestJson, CourseRequest.class);

        Set<ConstraintViolation<CourseRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        CourseResponse updated = courseService.updateCourse(id, request, thumbnail);
        return ResponseEntity.ok(updated);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id){
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable UUID id){
        return ResponseEntity.ok(courseService.publishCourse(id));
    }
}