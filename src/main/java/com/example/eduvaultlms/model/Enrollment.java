package com.example.eduvaultlms.model;

import com.example.eduvaultlms.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User studentId;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course courseId;

    @Column(updatable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(precision = 5, scale = 2)
    private Float completionPct = 0.0f;
}