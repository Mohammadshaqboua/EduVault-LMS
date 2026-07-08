package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.request.CourseRequest;
import com.example.eduvaultlms.dto.response.CourseResponse;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.CourseRepository;
import com.example.eduvaultlms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FileUploadService fileUploadService;

    public List<CourseResponse> getAllCourses() {
        return courseRepo.findAll()
                .stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(UUID id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return new CourseResponse(course);
    }

    public CourseResponse createCourse(CourseRequest request, MultipartFile thumbnail) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String url = fileUploadService.uploadCourseThumbnail(thumbnail);
            request.setThumbnailUrl(url);
        }
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());
        course.setIsPublished(false);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        course.setCreatedBy(currentUser);

        Course saved = courseRepo.save(course);
        return new CourseResponse(saved);
    }

    @Transactional
    public CourseResponse updateCourse(UUID id, CourseRequest request, MultipartFile thumbnail) {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String oldPublicId = course.getThumbnailPublicId();

            FileUploadService.UploadResult result =
                    fileUploadService.uploadCourseThumbnailWithId(thumbnail);

            course.setThumbnailUrl(result.url());
            course.setThumbnailPublicId(result.publicId());

            Course updated = courseRepo.save(course);

            if (oldPublicId != null && !oldPublicId.isBlank()) {
                fileUploadService.deleteFile(oldPublicId, "image");
            }

            return new CourseResponse(updated);
        }

        Course updated = courseRepo.save(course);
        return new CourseResponse(updated);
    }

    public void deleteCourse(UUID id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepo.delete(course);
    }

    public CourseResponse publishCourse(UUID id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsPublished(true);
        Course updated = courseRepo.save(course);
        return new CourseResponse(updated);
    }
}