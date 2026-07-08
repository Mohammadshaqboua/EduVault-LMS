package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.CourseProgressResponse;
import com.example.eduvaultlms.dto.response.LessonProgressResponse;
import com.example.eduvaultlms.enums.EnrollmentStatus;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.*;
import com.example.eduvaultlms.repository.*;
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

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuizRepository quizRepo;

    @Autowired
    private QuizResultRepository quizResultRepo;

    @Transactional
    public LessonProgressResponse completeLesson(UUID lessonId) {
        User currentUser = getCurrentUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        enrollmentRepository.findByStudentAndCourse(currentUser, lesson.getCourse())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You must be enrolled in this course before completing lessons"));

        Optional<LessonProgress> existing = lessonProgressRepo
                .findByStudentIdAndLessonId(currentUser, lesson);

        LessonProgress progress;

        if (existing.isPresent()) {
            progress = existing.get();
            progress.setIsCompleted(true);
            lessonProgressRepo.save(progress);
        } else {
            progress = new LessonProgress();
            progress.setStudentId(currentUser);
            progress.setLessonId(lesson);
            progress.setIsCompleted(true);
            lessonProgressRepo.save(progress);
        }

        updateEnrollmentCompletion(currentUser, lesson.getCourse());

        boolean IsQuizzesCompleted = allQuizzesPassed(currentUser.getId(), lesson.getCourse().getId());

        return toResponse(progress , IsQuizzesCompleted);
    }

    public CourseProgressResponse getCourseProgress(UUID courseId) {
        User currentUser = getCurrentUser();

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourse(currentUser, getCourseRef(courseId))
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
                .courseTitle(enrollment.getCourse().getTitle())
                .completedLessons((int) completedCount)
                .totalLessons((int) totalLessons)
                .completionPct(completionPct)
                .enrollmentStatus(enrollment.getStatus().name())
                .build();
    }

    public List<CourseProgressResponse> getMyProgress() {
        User currentUser = getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository
                .findByStudent(currentUser);


        return enrollments.stream().map(enrollment -> {
            UUID courseId = enrollment.getCourse().getId();

            long totalLessons = lessonRepository.countByCourseId(courseId);
            long completedCount = lessonProgressRepo
                    .countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(
                            currentUser, courseId);

            double completionPct = totalLessons > 0
                    ? (completedCount * 100.0 / totalLessons)
                    : 0.0;

            return CourseProgressResponse.builder()
                    .courseId(courseId)
                    .courseTitle(enrollment.getCourse().getTitle())
                    .completedLessons((int) completedCount)
                    .totalLessons((int) totalLessons)
                    .completionPct(completionPct)
                    .enrollmentStatus(enrollment.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    public void updateEnrollmentCompletion(User student, Course course) {
        enrollmentRepository.findByStudentAndCourse(student, course)
                .ifPresent(enrollment -> {
                    long totalLessons = lessonRepository.countByCourseId(course.getId());
                    long completedLessons = lessonProgressRepo
                            .countByStudentIdAndLessonId_Course_IdAndIsCompletedTrue(
                                    student, course.getId());

                    BigDecimal pct = totalLessons > 0
                            ? BigDecimal.valueOf(completedLessons * 100.0 / totalLessons)
                            : BigDecimal.ZERO;

                    enrollment.setCompletionPct(pct);

                    boolean allLessonsDone  = pct.compareTo(BigDecimal.valueOf(100)) >= 0;
                    boolean allQuizzesDone  = allQuizzesPassed(student.getId(), course.getId());

                    if (allLessonsDone && allQuizzesDone) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                        enrollmentRepository.save(enrollment);
                        certificateService.generateAndIssueCertificate(student, course);
                    } else {
                        enrollmentRepository.save(enrollment);
                    }
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

    private boolean allQuizzesPassed(UUID studentId, UUID courseId) {
        List<Quiz> quizzes = quizRepo.findByCourseId(courseId);

        if (quizzes.isEmpty()) {
            return true;
        }

        return quizzes.stream()
                .allMatch(quiz -> quizResultRepo
                        .findTopByStudentIdAndQuizIdOrderByAttemptNumberDesc(studentId, quiz.getId())
                        .map(QuizResult::isPassed)
                        .orElse(false));
    }

    private LessonProgressResponse toResponse(LessonProgress progress , boolean IsQuizzesCompleted) {
        return LessonProgressResponse.builder()
                .lessonId(progress.getLessonId().getId())
                .lessonTitle(progress.getLessonId().getTitle())
                .isCompleted(progress.getIsCompleted())
                .allQuizzesPassed(IsQuizzesCompleted)
                .watchedAt(progress.getWatchedAt())
                .watchedSeconds(progress.getWatchedSeconds())
                .build();
    }
}