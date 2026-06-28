package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.model.Certificate;
import com.example.eduvaultlms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findByStudentId(User student);
    Optional<Certificate> findByUniqueCode(String uniqueCode);
}