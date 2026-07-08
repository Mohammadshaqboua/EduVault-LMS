package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.request.QuizRequest;
import com.example.eduvaultlms.dto.request.QuizSubmitRequest;
import com.example.eduvaultlms.dto.response.QuizResponse;
import com.example.eduvaultlms.dto.response.QuizResultResponse;
import com.example.eduvaultlms.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/api/courses/{courseId}/quizzes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> createQuiz(
            @PathVariable UUID courseId,
            @Valid @RequestBody QuizRequest request) {

        QuizResponse created = quizService.createQuiz(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/quizzes/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResponse> getQuiz(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        QuizResponse quiz = quizService.getQuizForStudent(id, userDetails.getUsername());
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/quizzes/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable UUID id,
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        QuizResultResponse result = quizService.submitQuiz(id, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/quizzes/{id}/results")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<QuizResultResponse>> getMyResults(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<QuizResultResponse> results = quizService.getResultsForStudent(id, userDetails.getUsername());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/quizzes/{id}/all-results")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizResultResponse>> getAllResults(
            @PathVariable UUID id
    ) {
        List<QuizResultResponse> results = quizService.getAllResultsForQuiz(id);
        return ResponseEntity.ok(results);
    }
}