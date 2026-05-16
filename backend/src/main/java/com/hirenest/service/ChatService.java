package com.hirenest.service;

import com.hirenest.dto.ChatPartnerDto;
import com.hirenest.dto.MessageDto;
import com.hirenest.entity.Message;
import com.hirenest.entity.NotificationType;
import com.hirenest.entity.User;
import com.hirenest.exception.BadRequestException;
import com.hirenest.exception.ResourceNotFoundException;
import com.hirenest.repository.MessageRepository;
import com.hirenest.repository.UserRepository;
import com.hirenest.util.SecurityUtils;
import com.hirenest.websocket.ChatPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final ChatPublisher chatPublisher;
    private final NotificationService notificationService;

    public ChatService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            SecurityUtils securityUtils,
            ChatPublisher chatPublisher,
            NotificationService notificationService
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.chatPublisher = chatPublisher;
        this.notificationService = notificationService;
    }

    @Transactional
    public MessageDto sendMessage(Long receiverId, String content) {
        User sender = securityUtils.getCurrentUser();
        return sendMessageInternal(sender, receiverId, content);
    }

    @Transactional
    public MessageDto sendMessageByEmail(String senderEmail, Long receiverId, String content) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        return sendMessageInternal(sender, receiverId, content);
    }

    private MessageDto sendMessageInternal(User sender, Long receiverId, String content) {
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Message cannot be empty");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message = messageRepository.save(message);

        MessageDto dto = toDto(message);
        chatPublisher.sendMessage(receiverId, dto);
        chatPublisher.sendMessage(sender.getId(), dto);

        notificationService.create(
                receiverId,
                NotificationType.NEW_MESSAGE,
                "New Message",
                sender.getName() + ": " + content.substring(0, Math.min(50, content.length())),
                message.getId()
        );

        return dto;
    }

    public List<MessageDto> getConversation(Long partnerId) {
        User user = securityUtils.getCurrentUser();
        return messageRepository.findConversation(user.getId(), partnerId)
                .stream().map(this::toDto).toList();
    }

    public List<ChatPartnerDto> getChatPartners() {
        User user = securityUtils.getCurrentUser();
        List<Long> partnerIds = messageRepository.findConversationPartnerIds(user.getId());
        List<ChatPartnerDto> partners = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            User partner = userRepository.findById(partnerId).orElse(null);
            if (partner == null) continue;

            ChatPartnerDto dto = new ChatPartnerDto();
            dto.setUserId(partner.getId());
            dto.setName(partner.getName());
            dto.setProfileImageUrl(partner.getProfileImageUrl());
            dto.setOnline(partner.isOnline());

            List<Message> messages = messageRepository.findConversation(user.getId(), partnerId);
            if (!messages.isEmpty()) {
                Message last = messages.get(messages.size() - 1);
                dto.setLastMessage(last.getContent());
            }
            partners.add(dto);
        }

        partners.sort(Comparator.comparing(ChatPartnerDto::getName, String.CASE_INSENSITIVE_ORDER));
        return partners;
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setSenderId(m.getSender().getId());
        dto.setSenderName(m.getSender().getName());
        dto.setReceiverId(m.getReceiver().getId());
        dto.setContent(m.getContent());
        dto.setSentAt(m.getSentAt());
        dto.setRead(m.isRead());
        return dto;
    }
}
