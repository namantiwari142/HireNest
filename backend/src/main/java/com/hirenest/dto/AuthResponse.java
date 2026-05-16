package com.hirenest.dto;

import com.hirenest.entity.Role;

public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String name;
    private Role role;
    private String profileImageUrl;

    public AuthResponse(String token, Long userId, String email, String name, Role role, String profileImageUrl) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
