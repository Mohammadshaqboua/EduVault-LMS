package com.example.eduvaultlms.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private String instructorName;
}
