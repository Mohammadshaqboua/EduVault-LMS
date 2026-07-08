package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.request.LessonRequest;
import com.example.eduvaultlms.dto.response.LessonResponse;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Lesson;
import com.example.eduvaultlms.repository.CourseRepository;
import com.example.eduvaultlms.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository  lessonRepo;
    private final CourseRepository  courseRepo;
    private final FileUploadService fileUploadService;

    public List<LessonResponse> getLessonsByCourse(UUID courseId) {
        return lessonRepo.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(LessonResponse::new)
                .collect(Collectors.toList());
    }

    public LessonResponse createLesson(UUID courseId, LessonRequest request, MultipartFile video) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (video != null && !video.isEmpty()) {
            String url = fileUploadService.uploadLessonVideo(video);
            request.setVideoUrl(url);
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setCourse(course);

        return new LessonResponse(lessonRepo.save(lesson));
    }

    public LessonResponse updateLesson(UUID id, LessonRequest request, MultipartFile video) {
        Lesson lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex());

        if (video != null && !video.isEmpty()) {
            String videoUrl = fileUploadService.uploadLessonVideo(video);
            lesson.setVideoUrl(videoUrl);
        }

        Lesson updated = lessonRepo.save(lesson);

        return LessonResponse.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .content(updated.getContent())
                .videoUrl(updated.getVideoUrl())
                .duration(updated.getDuration())
                .orderIndex(updated.getOrderIndex())
                .courseId(updated.getCourse().getId())
                .build();
    }

    public LessonResponse reorderLesson(UUID id, int newOrderIndex) {
        Lesson lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));

        int oldOrderIndex = lesson.getOrderIndex();
        UUID courseId = lesson.getCourse().getId();

        if (newOrderIndex == oldOrderIndex) {
            return new LessonResponse(lesson);
        }

        List<Lesson> lessonsInCourse = lessonRepo.findByCourseId(courseId);

        if (newOrderIndex < oldOrderIndex) {
            for (Lesson l : lessonsInCourse) {
                if (!l.getId().equals(id) && l.getOrderIndex() >= newOrderIndex && l.getOrderIndex() < oldOrderIndex) {
                    l.setOrderIndex(l.getOrderIndex() + 1);
                    lessonRepo.save(l);
                }
            }
        } else {
            for (Lesson l : lessonsInCourse) {
                if (!l.getId().equals(id) && l.getOrderIndex() > oldOrderIndex && l.getOrderIndex() <= newOrderIndex) {
                    l.setOrderIndex(l.getOrderIndex() - 1);
                    lessonRepo.save(l);
                }
            }
        }

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