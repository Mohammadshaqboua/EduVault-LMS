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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository     courseRepository;
    private final UserRepository       userRepository;

    @Transactional
    public EnrollmentResponse enroll(UUID studentId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("The course is not available."));

        if (!course.getIsPublished()) {
            throw new ResourceNotFoundException("The course is currently unavailable.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new AlreadyEnrolledException("You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setCompletionPct(BigDecimal.ZERO);

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void unenroll(UUID studentId, UUID courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("The course is not available."));

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException("You are not enrolled in this course"));

        enrollmentRepository.delete(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("The course is not available."));

        return enrollmentRepository.findByCourse(course)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .enrollmentId(e.getId())
                .courseId(e.getCourse().getId())
                .courseTitle(e.getCourse().getTitle())
                .courseThumbnailUrl(e.getCourse().getThumbnailUrl())
                .coursePrice(e.getCourse().getPrice())
                .enrolledAt(e.getEnrolledAt())
                .status(e.getStatus())
                .completionPct(e.getCompletionPct())
                .build();
    }
}