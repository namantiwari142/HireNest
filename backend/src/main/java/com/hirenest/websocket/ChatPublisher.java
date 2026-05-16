package com.hirenest.websocket;

import com.hirenest.dto.MessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(Long receiverId, MessageDto message) {
        messagingTemplate.convertAndSend("/topic/chat/" + receiverId, message);
    }

    public void sendOnlineStatus(Long userId, boolean online) {
        messagingTemplate.convertAndSend("/topic/presence/" + userId, online);
    }
}
