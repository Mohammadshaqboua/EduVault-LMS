package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.CertificateResponse;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.exception.UnauthorizedException;
import com.example.eduvaultlms.model.Certificate;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.CertificateRepository;
import com.example.eduvaultlms.repository.EnrollmentRepository;
import com.example.eduvaultlms.repository.LessonProgressRepository;
import com.example.eduvaultlms.repository.LessonRepository;
import com.example.eduvaultlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    @Autowired
    private final CertificateRepository    certificateRepository;
    @Autowired
    private final UserRepository           userRepository;
    @Autowired
    private final EnrollmentRepository     enrollmentRepository;
    @Autowired
    private final LessonRepository         lessonRepository;
    @Autowired
    private final LessonProgressRepository lessonProgressRepository;
    @Autowired
    private final FileUploadService        fileUploadService;
    @Autowired
    private final EmailService             emailService;

    public void generateAndIssueCertificate(User student, Course course) {
        boolean alreadyIssued = certificateRepository
                .findByStudentId(student)
                .stream()
                .anyMatch(c -> c.getCourseId().getId().equals(course.getId()));

        if (alreadyIssued) return;

        String uniqueCode = UUID.randomUUID().toString();
        byte[] pdfBytes   = buildPdf(student.getName(), course.getTitle(), uniqueCode);
        String pdfUrl     = fileUploadService.uploadCertificatePdf(pdfBytes, uniqueCode);

        Certificate certificate = new Certificate();
        certificate.setStudentId(student);
        certificate.setCourseId(course);
        certificate.setPdfUrl(pdfUrl);
        certificate.setUniqueCode(uniqueCode);
        certificateRepository.save(certificate);

        try {
            emailService.sendCertificateEmail(
                    student.getEmail(), student.getName(), course.getTitle(), pdfUrl);
        } catch (Exception ignored) {}
    }

    public List<CertificateResponse> getMyCertificates(Authentication authentication) {
        User currentUser = resolveUser(authentication);
        return certificateRepository.findByStudentId(currentUser)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CertificateResponse getCertificateForDownload(UUID id, Authentication authentication) {
        User currentUser = resolveUser(authentication);
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found with id: " + id));
        if (!certificate.getStudentId().getId().equals(currentUser.getId()))
            throw new UnauthorizedException("You are not authorized to access this certificate");
        return toResponse(certificate);
    }

    public CertificateResponse verifyCertificate(String code) {
        Certificate certificate = certificateRepository.findByUniqueCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No certificate found with verification code: " + code));
        return toResponse(certificate);
    }

    private byte[] buildPdf(String studentName, String courseTitle, String uniqueCode) {
        try {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDFont bold   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            cs.beginText();
            cs.setFont(bold, 26);
            cs.newLineAtOffset(80, 720);
            cs.showText("Certificate of Completion");
            cs.endText();

            cs.beginText();
            cs.setFont(normal, 14);
            cs.newLineAtOffset(80, 670);
            cs.showText("This is to certify that");
            cs.endText();

            cs.beginText();
            cs.setFont(bold, 20);
            cs.newLineAtOffset(80, 630);
            cs.showText(studentName);
            cs.endText();

            cs.beginText();
            cs.setFont(normal, 14);
            cs.newLineAtOffset(80, 590);
            cs.showText("has successfully completed the course");
            cs.endText();

            cs.beginText();
            cs.setFont(bold, 18);
            cs.newLineAtOffset(80, 550);
            cs.showText(courseTitle);
            cs.endText();

            cs.beginText();
            cs.setFont(normal, 9);
            cs.newLineAtOffset(80, 80);
            cs.showText("Verification Code: " + uniqueCode);
            cs.endText();

            cs.close();

            java.io.File tempFile = java.io.File.createTempFile("cert-", ".pdf");
            doc.save(tempFile);
            doc.close();

            byte[] result = java.nio.file.Files.readAllBytes(tempFile.toPath());
            tempFile.delete();
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate PDF: " + e.getMessage());
        }
    }

    private User resolveUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException(
                        "Authenticated user not found: " + email));
    }

    private CertificateResponse toResponse(Certificate cert) {
        return new CertificateResponse(
                cert.getId(),
                cert.getStudentId().getName(),
                cert.getCourseId().getTitle(),
                cert.getPdfUrl(),
                cert.getUniqueCode(),
                cert.getIssuedAt());
    }
}