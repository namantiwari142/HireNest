package com.hirenest.service;

import com.hirenest.dto.NotificationDto;
import com.hirenest.entity.Notification;
import com.hirenest.entity.NotificationType;
import com.hirenest.entity.User;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.NotificationRepository;
import com.hirenest.repository.UserRepository;
import com.hirenest.websocket.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPublisher notificationPublisher;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationPublisher notificationPublisher
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public Notification create(Long userId, NotificationType type, String title, String message, Long referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setReferenceId(referenceId);
        n = notificationRepository.save(n);
        NotificationDto dto = toDto(n);
        notificationPublisher.sendToUser(userId, dto);
        return n;
    }

    public List<NotificationDto> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReferenceId(n.getReferenceId());
        return dto;
    }
}
