package com.example.eduvaultlms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse {
    private UUID id;
    private String studentName;
    private String courseTitle;
    private String pdfUrl;
    private String uniqueCode;
    private LocalDateTime issuedAt;
}