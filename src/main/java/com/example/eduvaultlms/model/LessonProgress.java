package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "lesson_progress")
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User studentId;

    @ManyToOne
    @JoinColumn(name = "lessonId", nullable = false)
    private Lesson lessonId;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(updatable = false)
    private LocalDateTime watchedAt = LocalDateTime.now();

    private Integer watchedSeconds = 0;
}