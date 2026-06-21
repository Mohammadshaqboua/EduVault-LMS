package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.response.CertificateResponse;
import com.example.eduvaultlms.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificateResponse>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getMyCertificates(authentication));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificateResponse> downloadCertificate(
            @PathVariable UUID id,
            Authentication authentication) {
        return ResponseEntity.ok(certificateService.getCertificateForDownload(id, authentication));
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<CertificateResponse> verifyCertificate(@PathVariable String code) {
        return ResponseEntity.ok(certificateService.verifyCertificate(code));
    }
}