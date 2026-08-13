package com.innovation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVO {

    private Long conversationId;

    private Integer otherUserId;

    private String otherUserName;

    private Integer unreadCount;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private Boolean online;
}
