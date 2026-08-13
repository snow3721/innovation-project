package com.innovation.controller;

import com.innovation.dto.ChatMessageVO;
import com.innovation.dto.ChatSendDTO;
import com.innovation.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void handleSend(ChatSendDTO dto, Principal principal) {
        if (principal == null) {
            return;
        }
        Integer senderId = Integer.parseInt(principal.getName());
        try {
            ChatMessageVO vo = chatService.sendMessage(senderId, dto.getReceiverId(), dto.getContent());
            // 推送给接收方
            messagingTemplate.convertAndSendToUser(dto.getReceiverId().toString(), "/queue/messages", vo);
            // 回送给发送方确认
            messagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/messages", vo);
        } catch (Exception e) {
            log.error("WebSocket消息处理失败: {}", e.getMessage());
        }
    }
}
