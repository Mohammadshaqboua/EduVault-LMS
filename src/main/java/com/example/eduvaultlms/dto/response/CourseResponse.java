package com.example.eduvaultlms.dto.response;

import com.example.eduvaultlms.model.Course;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    private UUID        id;
    private String      title;
    private String      description;
    private String      thumbnailUrl;
    private BigDecimal  price;
    private Boolean     isPublished;
    private UUID        createdById;

    public CourseResponse(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.thumbnailUrl = course.getThumbnailUrl();
        this.price = course.getPrice();
        this.isPublished = course.getIsPublished();
        this.createdById = course.getCreatedBy() != null ? course.getCreatedBy().getId() : null;
    }
}