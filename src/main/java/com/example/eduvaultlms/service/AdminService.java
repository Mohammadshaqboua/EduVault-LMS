package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.AdminStatsResponse;
import com.example.eduvaultlms.dto.response.CourseStatsResponse;
import com.example.eduvaultlms.dto.response.UserResponse;
import com.example.eduvaultlms.enums.Role;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private QuizRepository quizRepository;

    public AdminStatsResponse getStats() {
        long totalStudents        = userRepository.countByRole(Role.STUDENT);
        long totalCourses         = courseRepository.count();
        long publishedCourses     = courseRepository.countByIsPublishedTrue();
        long totalEnrollments     = enrollmentRepository.count();
        long totalCertificates    = certificateRepository.count();
        long totalQuizzes         = quizRepository.count();

        Double avgCompletion = enrollmentRepository.avgCompletionPctPlatform();
        double averageCompletionRate = (avgCompletion != null) ? avgCompletion : 0.0;

        return AdminStatsResponse.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .totalEnrollments(totalEnrollments)
                .totalCertificatesIssued(totalCertificates)
                .totalQuizzes(totalQuizzes)
                .averageCompletionRate(averageCompletionRate)
                .build();
    }

    public List<UserResponse> getAllStudents() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.STUDENT)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllAdmins() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.ADMIN)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }


    public List<CourseStatsResponse> getCourseStats() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(course -> {
                    UUID courseId = course.getId();
                    long totalEnrollments  = enrollmentRepository.countByCourseId(courseId);
                    Double avgPct          = enrollmentRepository.avgCompletionPctByCourseId(courseId);
                    long completedStudents = enrollmentRepository.countCompletedStudentsByCourseId(courseId);
                    long totalLessons      = courseRepository.countLessonsByCourseId(courseId);
                    long totalQuizzes      = courseRepository.countQuizzesByCourseId(courseId);

                    return CourseStatsResponse.builder()
                            .courseId(courseId)
                            .courseTitle(course.getTitle())
                            .totalEnrollments(totalEnrollments)
                            .averageCompletionPct(avgPct != null ? avgPct : 0.0)
                            .completedStudents(completedStudents)
                            .totalLessons(totalLessons)
                            .totalQuizzes(totalQuizzes)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}