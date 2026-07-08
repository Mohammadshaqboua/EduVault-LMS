package com.example.eduvaultlms.dto.request;

import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {
    @NotBlank @Size(max = 200)
    private String title;

    private String videoUrl;

    private String content;

    private Integer duration;

    @NotNull @Min(1)
    private Integer orderIndex;
}