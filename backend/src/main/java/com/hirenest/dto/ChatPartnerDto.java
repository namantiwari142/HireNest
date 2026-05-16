package com.hirenest.dto;

public class ChatPartnerDto {
    private Long userId;
    private String name;
    private String profileImageUrl;
    private boolean online;
    private String lastMessage;
    private Long unreadCount;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public Long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }
}
