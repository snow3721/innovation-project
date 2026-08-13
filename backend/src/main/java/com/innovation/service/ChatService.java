package com.innovation.service;

import com.innovation.dto.ChatMessageVO;
import com.innovation.dto.ConversationVO;

import java.util.List;

public interface ChatService {

    List<ConversationVO> listConversations(Integer userId);

    List<ChatMessageVO> getMessages(Long conversationId, Integer userId, int page, int size);

    ChatMessageVO sendMessage(Integer senderId, Integer receiverId, String content);

    void deleteConversation(Long conversationId, Integer userId);

    void markRead(Long conversationId, Integer userId);

    Integer getOtherParticipant(Long conversationId, Integer userId);
}
