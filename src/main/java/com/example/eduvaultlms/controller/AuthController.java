package com.example.eduvaultlms.controller;

import com.example.eduvaultlms.dto.request.LoginRequest;
import com.example.eduvaultlms.dto.request.RefreshTokenRequest;
import com.example.eduvaultlms.dto.request.RegisterRequest;
import com.example.eduvaultlms.dto.response.AuthResponse;
import com.example.eduvaultlms.dto.response.MessageResponse;
import com.example.eduvaultlms.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public MessageResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request){
        return authService.refreshToken(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public MessageResponse logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return authService.logout(token);
    }
}
