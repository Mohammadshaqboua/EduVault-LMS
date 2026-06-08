package com.example.eduvaultlms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseRequest {
    @NotBlank @Size(max = 200)
    private String title;

    private String description;

    private String thumbnailUrl;

    @NotNull
    private BigDecimal price;
}