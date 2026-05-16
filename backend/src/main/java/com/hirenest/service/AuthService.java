package com.hirenest.service;

import com.hirenest.dto.AuthRequest;
import com.hirenest.dto.AuthResponse;
import com.hirenest.dto.RegisterRequest;
import com.hirenest.entity.*;
import com.hirenest.exception.BadRequestException;
import com.hirenest.repository.ApplicantRepository;
import com.hirenest.repository.RecruiterRepository;
import com.hirenest.repository.UserRepository;
import com.hirenest.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final RecruiterRepository recruiterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            ApplicantRepository applicantRepository,
            RecruiterRepository recruiterRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.applicantRepository = applicantRepository;
        this.recruiterRepository = recruiterRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot self-register as admin");
        }
        if (request.getRole() == Role.RECRUITER &&
                (request.getCompanyName() == null || request.getCompanyName().isBlank())) {
            throw new BadRequestException("Company name is required for recruiters");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setProvider(AuthProvider.LOCAL);
        user = userRepository.save(user);

        if (request.getRole() == Role.RECRUITER) {
            Recruiter recruiter = new Recruiter();
            recruiter.setUser(user);
            recruiter.setCompanyName(request.getCompanyName());
            recruiterRepository.save(recruiter);
        } else {
            Applicant applicant = new Applicant();
            applicant.setUser(user);
            applicantRepository.save(applicant);
        }

        return buildAuthResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    public AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

    public AuthResponse getCurrentUserInfo(User user) {
        return toAuthResponse(user, null);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(),
                user.getRole(), user.getProfileImageUrl());
    }
}
