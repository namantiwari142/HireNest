package com.hirenest.websocket;

import com.hirenest.dto.MessageDto;
import com.hirenest.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(Principal principal, @Payload Map<String, Object> payload) {
        if (principal == null) return;
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = payload.get("content").toString();
        chatService.sendMessageByEmail(principal.getName(), receiverId, content);
    }
}
