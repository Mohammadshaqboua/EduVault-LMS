package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    Optional<Certificate> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    Optional<Certificate> findByUniqueCode(String uniqueCode);
    List<Certificate> findByStudentId(UUID studentId);
}