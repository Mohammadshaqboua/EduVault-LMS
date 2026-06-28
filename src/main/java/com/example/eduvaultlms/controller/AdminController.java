package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.response.AdminStatsResponse;
import com.example.eduvaultlms.dto.response.CourseStatsResponse;
import com.example.eduvaultlms.dto.response.UserResponse;
import com.example.eduvaultlms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> toggleUser(@PathVariable UUID id) {
        adminService.toggleUser(id);
        return ResponseEntity.ok("User status updated");
    }

    @GetMapping("/courses/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseStatsResponse> getCourseStats() {
        return ResponseEntity.ok(adminService.getCourseStats());
    }
}