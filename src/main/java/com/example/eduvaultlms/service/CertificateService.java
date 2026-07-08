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

import java.awt.Color;
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
    private final FileUploadService        fileUploadService;
    @Autowired
    private final EmailService             emailService;

    private static final Color COLOR_BACKGROUND   = new Color(252, 251, 245);
    private static final Color COLOR_NAVY         = new Color(30, 58, 95);
    private static final Color COLOR_GOLD         = new Color(197, 160, 89);
    private static final Color COLOR_GRAY_DARK    = new Color(90, 90, 90);
    private static final Color COLOR_GRAY_MEDIUM  = new Color(80, 80, 80);
    private static final Color COLOR_GRAY_LIGHT   = new Color(120, 120, 120);
    private static final Color COLOR_GRAY_LIGHTER = new Color(140, 140, 140);

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

            PDRectangle landscape = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage page = new PDPage(landscape);
            doc.addPage(page);

            float pageWidth = landscape.getWidth();
            float pageHeight = landscape.getHeight();

            PDFont bold       = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont boldItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);
            PDFont normal     = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont italic     = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            cs.setNonStrokingColor(COLOR_BACKGROUND);
            cs.addRect(0, 0, pageWidth, pageHeight);
            cs.fill();

            cs.setStrokingColor(COLOR_NAVY);
            cs.setLineWidth(3f);
            cs.addRect(25, 25, pageWidth - 50, pageHeight - 50);
            cs.stroke();

            cs.setStrokingColor(COLOR_GOLD);
            cs.setLineWidth(1f);
            cs.addRect(35, 35, pageWidth - 70, pageHeight - 70);
            cs.stroke();

            String title = "CERTIFICATE";
            float titleSize = 34;
            float titleWidth = bold.getStringWidth(title) / 1000 * titleSize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_NAVY);
            cs.setFont(bold, titleSize);
            cs.newLineAtOffset((pageWidth - titleWidth) / 2, pageHeight - 100);
            cs.showText(title);
            cs.endText();

            String subtitle = "O F   C O M P L E T I O N";
            float subtitleSize = 13;
            float subtitleWidth = normal.getStringWidth(subtitle) / 1000 * subtitleSize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_GOLD);
            cs.setFont(normal, subtitleSize);
            cs.newLineAtOffset((pageWidth - subtitleWidth) / 2, pageHeight - 125);
            cs.showText(subtitle);
            cs.endText();

            float lineY = pageHeight - 145;
            cs.setStrokingColor(COLOR_GOLD);
            cs.setLineWidth(1.2f);
            cs.moveTo(pageWidth / 2 - 40, lineY);
            cs.lineTo(pageWidth / 2 + 40, lineY);
            cs.stroke();

            String certifyText = "This is to certify that";
            float certifySize = 13;
            float certifyWidth = italic.getStringWidth(certifyText) / 1000 * certifySize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_GRAY_DARK);
            cs.setFont(italic, certifySize);
            cs.newLineAtOffset((pageWidth - certifyWidth) / 2, pageHeight - 195);
            cs.showText(certifyText);
            cs.endText();

            float nameSize = 28;
            float nameWidth = boldItalic.getStringWidth(studentName) / 1000 * nameSize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_NAVY);
            cs.setFont(boldItalic, nameSize);
            cs.newLineAtOffset((pageWidth - nameWidth) / 2, pageHeight - 235);
            cs.showText(studentName);
            cs.endText();

            float nameLineWidth = Math.max(nameWidth + 60, 250);
            cs.setStrokingColor(COLOR_GOLD);
            cs.setLineWidth(0.8f);
            cs.moveTo((pageWidth - nameLineWidth) / 2, pageHeight - 248);
            cs.lineTo((pageWidth + nameLineWidth) / 2, pageHeight - 248);
            cs.stroke();

            String completedText = "has successfully completed the course";
            float completedSize = 13;
            float completedWidth = italic.getStringWidth(completedText) / 1000 * completedSize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_GRAY_DARK);
            cs.setFont(italic, completedSize);
            cs.newLineAtOffset((pageWidth - completedWidth) / 2, pageHeight - 280);
            cs.showText(completedText);
            cs.endText();

            float courseSize = 20;
            float courseWidth = bold.getStringWidth(courseTitle) / 1000 * courseSize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_NAVY);
            cs.setFont(bold, courseSize);
            cs.newLineAtOffset((pageWidth - courseWidth) / 2, pageHeight - 315);
            cs.showText(courseTitle);
            cs.endText();

            String dateText = "Date: " + java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            cs.beginText();
            cs.setNonStrokingColor(COLOR_GRAY_MEDIUM);
            cs.setFont(normal, 10);
            cs.newLineAtOffset(80, 90);
            cs.showText(dateText);
            cs.endText();

            cs.setStrokingColor(COLOR_GRAY_LIGHT);
            cs.setLineWidth(0.7f);
            cs.moveTo(pageWidth - 220, 100);
            cs.lineTo(pageWidth - 80, 100);
            cs.stroke();

            cs.beginText();
            cs.setNonStrokingColor(COLOR_GRAY_MEDIUM);
            cs.setFont(italic, 10);
            cs.newLineAtOffset(pageWidth - 205, 85);
            cs.showText("Authorized Signature");
            cs.endText();
            
            String verifyText = "Verification Code: " + uniqueCode;
            float verifySize = 8.5f;
            float verifyWidth = normal.getStringWidth(verifyText) / 1000 * verifySize;
            cs.beginText();
            cs.setNonStrokingColor(COLOR_GRAY_LIGHTER);
            cs.setFont(normal, verifySize);
            cs.newLineAtOffset((pageWidth - verifyWidth) / 2, 45);
            cs.showText(verifyText);
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