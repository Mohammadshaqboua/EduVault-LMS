package com.example.eduvaultlms.repository;

import com.example.eduvaultlms.enums.Role;
import com.example.eduvaultlms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail (String email);
    List<User> findAllByOrderByCreatedAtDesc();

    long countByRole(Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(Role role);
}
