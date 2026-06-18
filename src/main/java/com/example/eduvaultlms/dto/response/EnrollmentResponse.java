package com.example.eduvaultlms.dto.response;

import com.example.eduvaultlms.enums.EnrollmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EnrollmentResponse {

    private UUID enrollmentId;

    private UUID courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    private BigDecimal coursePrice;

    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private BigDecimal completionPct;
}
