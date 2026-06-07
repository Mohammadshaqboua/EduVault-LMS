package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , length = 200)
    private String title;

    @ManyToOne
    @JoinColumn(name = "courseId" , nullable = false)
    private Course courseId;

    @Column(nullable = false)
    private Integer passMark;

    private Integer timeLimitMin;
}
