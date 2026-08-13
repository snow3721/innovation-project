package com.innovation.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id;

    @Indexed
    private Long conversationId;

    private Integer senderId;

    private String content;

    private LocalDateTime sendTime;

    public ChatMessage() {}

    public ChatMessage(Long conversationId, Integer senderId, String content, LocalDateTime sendTime) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.sendTime = sendTime;
    }
}
