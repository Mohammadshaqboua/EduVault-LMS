package com.example.eduvaultlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User studentId;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course courseId;

    private String pdfUrl;

    @Column(updatable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(unique = true, nullable = false)
    private String uniqueCode = UUID.randomUUID().toString();
}