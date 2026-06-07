package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quizId", nullable = false)
    private Quiz quizId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(columnDefinition = "JSON")
    private String options;

    @Column(nullable = false)
    private Integer correctIndex;

    @Column(nullable = false)
    private Integer points;
}