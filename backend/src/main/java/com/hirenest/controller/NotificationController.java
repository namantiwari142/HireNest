package com.hirenest.controller;

import com.hirenest.dto.ApiResponse;
import com.hirenest.dto.NotificationDto;
import com.hirenest.service.NotificationService;
import com.hirenest.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    public NotificationController(NotificationService notificationService, SecurityUtils securityUtils) {
        this.notificationService = notificationService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> list() {
        Long userId = securityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getForUser(userId)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        Long userId = securityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", notificationService.getUnreadCount(userId))));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id, securityUtils.getCurrentUser().getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllAsRead(securityUtils.getCurrentUser().getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
