package com.example.eduvaultlms.dto.response;

import com.example.eduvaultlms.model.Lesson;
import lombok.Data;

import java.util.UUID;

@Data
public class LessonResponse {
    private UUID id;
    private String title;
    private String videoUrl;
    private String content;
    private Integer duration;
    private Integer orderIndex;
    private UUID courseId;

    public LessonResponse(Lesson lesson) {
        this.id = lesson.getId();
        this.title = lesson.getTitle();
        this.videoUrl = lesson.getVideoUrl();
        this.content = lesson.getContent();
        this.duration = lesson.getDuration();
        this.orderIndex = lesson.getOrderIndex();
        this.courseId = lesson.getCourse().getId();
    }
}