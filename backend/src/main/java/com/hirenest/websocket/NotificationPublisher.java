package com.hirenest.websocket;

import com.hirenest.dto.NotificationDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(Long userId, NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
}
