package com.hirenest.service;

import com.hirenest.dto.ChatPartnerDto;
import com.hirenest.entity.Role;
import com.hirenest.entity.User;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ChatPartnerDto> searchUsers(String query, Long currentUserId, Role currentUserRole) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        Role targetRole = currentUserRole == Role.APPLICANT ? Role.RECRUITER : Role.APPLICANT;
        return userRepository.searchByNameOrEmail(query.trim(), targetRole, currentUserId).stream()
                .map(this::toChatPartner)
                .toList();
    }

    private ChatPartnerDto toChatPartner(User u) {
        ChatPartnerDto dto = new ChatPartnerDto();
        dto.setUserId(u.getId());
        dto.setName(u.getName());
        dto.setProfileImageUrl(u.getProfileImageUrl());
        dto.setOnline(u.isOnline());
        return dto;
    }

    @Transactional
    public void updateProfileImage(Long userId, String url) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setProfileImageUrl(url);
        userRepository.save(user);
    }

    @Transactional
    public void setOnline(Long userId, boolean online) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setOnline(online);
            userRepository.save(u);
        });
    }
}
