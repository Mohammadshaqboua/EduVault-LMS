package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer duration;

    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "courseId" , nullable = false)
    private Course courseId;
}
