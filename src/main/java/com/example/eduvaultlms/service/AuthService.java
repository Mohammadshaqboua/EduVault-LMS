package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.response.AuthResponse;
import com.example.eduvaultlms.dto.response.MessageResponse;
import com.example.eduvaultlms.dto.request.LoginRequest;
import com.example.eduvaultlms.dto.request.RegisterRequest;
import com.example.eduvaultlms.enums.Role;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public MessageResponse register(RegisterRequest request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        userRepo.save(user);

        return new MessageResponse("User registered successfully");
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken,refreshToken,user.getName(),user.getRole().name());

    }

    public AuthResponse refreshToken(String refreshToken){
        String email = jwtService.extractEmail(refreshToken);

        if(!jwtService.isTokenValid(refreshToken)){
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(email);

        return new AuthResponse(newAccessToken, refreshToken, user.getName(), user.getRole().name());
    }

    public MessageResponse logout(String token) {
        return new MessageResponse("Logged out successfully");
    }

}
