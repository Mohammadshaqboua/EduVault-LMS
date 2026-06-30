package com.example.eduvaultlms.dto.response;

import com.example.eduvaultlms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID            id;
    private String          name;
    private String          email;
    private Role            role;
    private boolean         isActive;
    private LocalDateTime   createdAt;
}