package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.CourseProgressResponse;
import com.example.eduvaultlms.dto.response.LessonProgressResponse;
import com.example.eduvaultlms.enums.EnrollmentStatus;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.*;
import com.example.eduvaultlms.repository.EnrollmentRepository;
import com.example.eduvaultlms.repository.LessonProgressRepository;
import com.example.eduvaultlms.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private LessonProgressRepository lessonProgressRepo;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Transactional
    public LessonProgressResponse completeLesson(UUID lessonId) {
        User currentUser = getCurrentUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Optional<LessonProgress> existing = lessonProgressRepo
                .findByStudentIdAndLessonId(currentUser, lesson);

        LessonProgress progress;

        if (existing.isPresent()) {
            progress = existing.get();
            progress.setIsCompleted(true);
        } else {
            progress = new LessonProgress();
            progress.setStudentId(currentUser);
            progress.setLessonId(lesson);
            progress.setIsCompleted(true);
            lessonProgressRepo.save(progress);
        }

        updateEnrollmentCompletion(currentUser, lesson.getCourse());

        return toResponse(progress);
    }

    public CourseProgressResponse getCourseProgress(UUID courseId) {
        User currentUser = getCurrentUser();

        // ✅ نمرر currentUser (object) بدل currentUser.getId()
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(currentUser, getCourseRef(courseId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for courseId: " + courseId));

        long totalLessons = lessonRepository.countByCourseId(courseId);

        long completedCount = lessonProgressRepo
                .countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(
                        currentUser, courseId);

        double completionPct = totalLessons > 0
                ? (completedCount * 100.0 / totalLessons)
                : 0.0;

        return CourseProgressResponse.builder()
                .courseId(courseId)
                .completedLessons((int) completedCount)
                .totalLessons((int) totalLessons)
                .completionPct(completionPct)
                .enrollmentStatus(enrollment.getStatus().name())
                .build();
    }

    public List<CourseProgressResponse> getMyProgress() {
        User currentUser = getCurrentUser();

        // ✅ نمرر currentUser (object) بدل currentUser.getId()
        List<Enrollment> enrollments = enrollmentRepository
                .findByStudentId(currentUser);

        return enrollments.stream().map(enrollment -> {
            UUID courseId = enrollment.getCourseId().getId();

            long totalLessons = lessonRepository.countByCourseId(courseId);
            long completedCount = lessonProgressRepo
                    .countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(
                            currentUser, courseId);

            double completionPct = totalLessons > 0
                    ? (completedCount * 100.0 / totalLessons)
                    : 0.0;

            return CourseProgressResponse.builder()
                    .courseId(courseId)
                    .completedLessons((int) completedCount)
                    .totalLessons((int) totalLessons)
                    .completionPct(completionPct)
                    .enrollmentStatus(enrollment.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    private void updateEnrollmentCompletion(User student, Course course) {
        // ✅ نمرر student و course (objects) بدل .getId()
        enrollmentRepository.findByStudentIdAndCourseId(student, course)
                .ifPresent(enrollment -> {
                    long total = lessonRepository.countByCourseId(course.getId());
                    long completed = lessonProgressRepo
                            .countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(
                                    student, course.getId());

                    BigDecimal pct = total > 0
                            ? BigDecimal.valueOf(completed * 100.0 / total)
                            : BigDecimal.ZERO;

                    enrollment.setCompletionPct(pct);

                    if (pct.compareTo(BigDecimal.valueOf(100)) >= 0) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    }

                    enrollmentRepository.save(enrollment);
                });
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private Course getCourseRef(UUID courseId) {
        Course course = new Course();
        course.setId(courseId);
        return course;
    }

    private LessonProgressResponse toResponse(LessonProgress progress) {
        return LessonProgressResponse.builder()
                .lessonId(progress.getLessonId().getId())
                .lessonTitle(progress.getLessonId().getTitle())
                .isCompleted(progress.getIsCompleted())
                .watchedAt(progress.getWatchedAt())
                .watchedSeconds(progress.getWatchedSeconds())
                .build();
    }
}