package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.request.LessonRequest;
import com.example.eduvaultlms.dto.response.LessonResponse;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Lesson;
import com.example.eduvaultlms.repository.CourseRepository;
import com.example.eduvaultlms.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private CourseRepository courseRepo;

    public List<LessonResponse> getLessonsByCourse(UUID courseId) {
        return lessonRepo.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(LessonResponse::new)
                .collect(Collectors.toList());
    }

    public LessonResponse createLesson(UUID courseId, LessonRequest request) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setCourse(course);

        Lesson saved = lessonRepo.save(lesson);
        return new LessonResponse(saved);
    }

    public LessonResponse updateLesson(UUID id, LessonRequest request) {
        Lesson lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex());

        Lesson updated = lessonRepo.save(lesson);
        return new LessonResponse(updated);
    }

    public LessonResponse reorderLesson(UUID id, int newOrderIndex) {
        Lesson lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));

        lesson.setOrderIndex(newOrderIndex);
        Lesson updated = lessonRepo.save(lesson);
        return new LessonResponse(updated);
    }

    public void deleteLesson(UUID id) {
        Lesson lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        lessonRepo.delete(lesson);
    }
}