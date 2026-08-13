package com.innovation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageVO {

    private String id;

    private Long conversationId;

    private Integer senderId;

    private String content;

    private LocalDateTime sendTime;
}
