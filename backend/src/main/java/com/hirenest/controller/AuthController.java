package com.hirenest.controller;

import com.hirenest.dto.*;
import com.hirenest.service.AuthService;
import com.hirenest.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityUtils securityUtils;

    public AuthController(AuthService authService, SecurityUtils securityUtils) {
        this.authService = authService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> me() {
        return ResponseEntity.ok(ApiResponse.ok(
                authService.getCurrentUserInfo(securityUtils.getCurrentUser())));
    }
}
