package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.EnrollmentResponse;
import com.example.eduvaultlms.enums.EnrollmentStatus;
import com.example.eduvaultlms.exception.AlreadyEnrolledException;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Enrollment;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.CourseRepository;
import com.example.eduvaultlms.repository.EnrollmentRepository;
import com.example.eduvaultlms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;

    @Transactional
    public EnrollmentResponse enroll(UUID studentId, UUID courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("The course is not available."));

        if (!course.getIsPublished()) {
            throw new ResourceNotFoundException("The course is currently unavailable.ً");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyEnrolledException("You are already enrolled in this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student);
        enrollment.setCourseId(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setCompletionPct(BigDecimal.ZERO);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void unenroll(UUID studentId, UUID courseId) {

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not enrolled in this course"));

        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(UUID courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("The course is not available.");
        }

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .enrollmentId(e.getId())
                .courseId(e.getCourseId().getId())
                .courseTitle(e.getCourseId().getTitle())
                .courseThumbnailUrl(e.getCourseId().getThumbnailUrl())
                .coursePrice(e.getCourseId().getPrice())
                .enrolledAt(e.getEnrolledAt())
                .status(e.getStatus())
                .completionPct(e.getCompletionPct())
                .build();
    }
}
