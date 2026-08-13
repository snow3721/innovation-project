package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.dto.ChatMessageVO;
import com.innovation.dto.ChatSendDTO;
import com.innovation.dto.ConversationVO;
import com.innovation.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "站内聊天")
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @ApiOperation("获取会话列表")
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> listConversations() {
        Integer userId = currentUserId();
        return Result.success(chatService.listConversations(userId));
    }

    @ApiOperation("获取会话消息历史")
    @GetMapping("/conversations/{conversationId}/messages")
    public Result<List<ChatMessageVO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Integer userId = currentUserId();
        return Result.success(chatService.getMessages(conversationId, userId, page, size));
    }

    @ApiOperation("发送消息")
    @PostMapping("/messages")
    public Result<ChatMessageVO> sendMessage(@Validated @RequestBody ChatSendDTO dto) {
        Integer userId = currentUserId();
        ChatMessageVO vo = chatService.sendMessage(userId, dto.getReceiverId(), dto.getContent());
        messagingTemplate.convertAndSendToUser(dto.getReceiverId().toString(), "/queue/messages", vo);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/messages", vo);
        return Result.success(vo);
    }

    @ApiOperation("标记会话已读")
    @PutMapping("/conversations/{conversationId}/read")
    public Result<Void> markRead(@PathVariable Long conversationId) {
        Integer userId = currentUserId();
        chatService.markRead(conversationId, userId);
        return Result.success();
    }

    @ApiOperation("删除会话")
    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable Long conversationId) {
        Integer userId = currentUserId();
        chatService.deleteConversation(conversationId, userId);
        return Result.success();
    }

    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Integer) auth.getPrincipal();
    }
}
