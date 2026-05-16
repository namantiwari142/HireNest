package com.hirenest.controller;

import com.hirenest.dto.ApiResponse;
import com.hirenest.dto.ChatPartnerDto;
import com.hirenest.dto.MessageDto;
import com.hirenest.service.ChatService;
import com.hirenest.service.UserService;
import com.hirenest.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final SecurityUtils securityUtils;
    private final UserService userService;

    public ChatRestController(ChatService chatService, SecurityUtils securityUtils, UserService userService) {
        this.chatService = chatService;
        this.securityUtils = securityUtils;
        this.userService = userService;
    }

    @GetMapping("/partners")
    public ResponseEntity<ApiResponse<List<ChatPartnerDto>>> partners() {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getChatPartners()));
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<ChatPartnerDto>>> searchUsers(@RequestParam String q) {
        var current = securityUtils.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(
                userService.searchUsers(q, current.getId(), current.getRole())));
    }

    @GetMapping("/conversation/{partnerId}")
    public ResponseEntity<ApiResponse<List<MessageDto>>> conversation(@PathVariable Long partnerId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getConversation(partnerId)));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageDto>> send(@RequestBody Map<String, Object> body) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = body.get("content").toString();
        return ResponseEntity.ok(ApiResponse.ok(chatService.sendMessage(receiverId, content)));
    }

    @PostMapping("/online")
    public ResponseEntity<ApiResponse<Void>> setOnline(@RequestParam boolean online) {
        userService.setOnline(securityUtils.getCurrentUser().getId(), online);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
