package com.example.eduvaultlms.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_IMAGE_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");

    private static final List<String> ALLOWED_VIDEO_TYPES =
            List.of("video/mp4", "video/webm", "video/quicktime");

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 200L * 1024 * 1024;

    public String uploadCourseThumbnail(MultipartFile file) {
        validateImage(file);
        return upload(file, "eduvault/thumbnails", "auto");
    }

    public String uploadLessonVideo(MultipartFile file) {
        validateVideo(file);
        return upload(file, "eduvault/videos", "video");
    }

    public String uploadCertificatePdf(byte[] pdfBytes, String certificateCode) {
        try {
            String base64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
            String dataUri = "data:application/pdf;base64," + base64;

            Map<String, Object> options = ObjectUtils.asMap(
                    "folder",        "eduvault/certificates",
                    "resource_type", "raw",
                    "public_id",     certificateCode,
                    "format",        "pdf",
                    "overwrite",     true,
                    "access_mode",   "public"
            );

            Map<?, ?> result = cloudinary.uploader().upload(dataUri, options);
            String url = (String) result.get("secure_url");

            if (url == null || url.isEmpty()) {
                throw new RuntimeException("Cloudinary returned no URL");
            }

            return url;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload certificate, please try again");
        }
    }

    private String upload(MultipartFile file, String folder, String resourceType) {
        try {
            String publicId = UUID.randomUUID().toString();
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder",        folder,
                    "resource_type", resourceType,
                    "public_id",     publicId,
                    "overwrite",     false
            );
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file, please try again");
        }
    }

    public void deleteFile(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", resourceType));
        } catch (IOException e) {
            // silent fail
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Thumbnail file is empty or missing");
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Unsupported image type (allowed: jpeg, png, webp)");
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new IllegalArgumentException("Image exceeds the 5MB size limit");
    }

    private void validateVideo(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Video file is empty or missing");
        if (!ALLOWED_VIDEO_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Unsupported video type (allowed: mp4, webm, mov)");
        if (file.getSize() > MAX_VIDEO_SIZE)
            throw new IllegalArgumentException("Video exceeds the 200MB size limit");
    }
}