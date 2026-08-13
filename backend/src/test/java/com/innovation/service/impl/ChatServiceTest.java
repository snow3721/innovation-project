package com.innovation.service.impl;

import com.innovation.dto.ChatMessageVO;
import com.innovation.entity.ChatMessage;
import com.innovation.entity.Conversation;
import com.innovation.entity.ConversationParticipant;
import com.innovation.mapper.ConversationMapper;
import com.innovation.mapper.ConversationParticipantMapper;
import com.innovation.mapper.UserMapper;
import com.innovation.repository.ChatMessageRepository;
import com.innovation.util.OnlineStatusManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private ConversationMapper conversationMapper;
    private ConversationParticipantMapper participantMapper;
    private UserMapper userMapper;
    private ChatMessageRepository chatMessageRepository;
    private OnlineStatusManager onlineStatusManager;
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        conversationMapper = mock(ConversationMapper.class);
        participantMapper = mock(ConversationParticipantMapper.class);
        userMapper = mock(UserMapper.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        onlineStatusManager = mock(OnlineStatusManager.class);
        chatService = new ChatServiceImpl(conversationMapper, participantMapper,
                userMapper, chatMessageRepository, onlineStatusManager);
    }

    @Test
    void testSendMessageCreatesNewConversation() {
        Integer senderId = 1;
        Integer receiverId = 2;
        String content = "hello";

        when(participantMapper.findCommonConversation(senderId, receiverId)).thenReturn(null);
        when(conversationMapper.insert(any(Conversation.class))).thenAnswer(inv -> {
            Conversation c = inv.getArgument(0);
            c.setConversationId(100L);
            return 1;
        });
        when(participantMapper.selectOne(any())).thenReturn(null);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage m = inv.getArgument(0);
            m.setId("msg1");
            return m;
        });

        ChatMessageVO vo = chatService.sendMessage(senderId, receiverId, content);

        assertNotNull(vo);
        assertEquals(senderId, vo.getSenderId());
        assertEquals(content, vo.getContent());
        assertEquals(100L, vo.getConversationId());
        verify(participantMapper, times(2)).insert(any(ConversationParticipant.class));
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void testSendMessageReusesExistingConversation() {
        Integer senderId = 1;
        Integer receiverId = 2;
        Long conversationId = 50L;
        String content = "hi again";

        when(participantMapper.findCommonConversation(senderId, receiverId)).thenReturn(conversationId);
        when(participantMapper.selectOne(any())).thenReturn(null);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage m = inv.getArgument(0);
            m.setId("msg2");
            return m;
        });

        ChatMessageVO vo = chatService.sendMessage(senderId, receiverId, content);

        assertEquals(conversationId, vo.getConversationId());
        verify(conversationMapper, never()).insert(any(Conversation.class));
        verify(participantMapper, never()).insert(any(ConversationParticipant.class));
    }

    @Test
    void testSendMessageToSelfThrows() {
        assertThrows(RuntimeException.class,
                () -> chatService.sendMessage(1, 1, "self"));
    }

    @Test
    void testGetMessagesAccessDeniedForNonParticipant() {
        when(participantMapper.selectCount(any())).thenReturn(0L);
        assertThrows(RuntimeException.class,
                () -> chatService.getMessages(10L, 1, 1, 20));
        verify(chatMessageRepository, never()).findByConversationIdOrderBySendTimeDesc(any(), any(PageRequest.class));
    }

    @Test
    void testListConversations() {
        Integer userId = 1;
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversationId(10L);
        participant.setUserId(userId);
        participant.setUnreadCount(2);
        participant.setDeleted(0);

        when(participantMapper.findActiveByUserId(userId)).thenReturn(Collections.singletonList(participant));
        when(participantMapper.findOtherParticipant(10L, userId)).thenReturn(2);
        ChatMessage last = new ChatMessage(10L, 2, "last", LocalDateTime.now());
        when(chatMessageRepository.findByConversationIdOrderBySendTimeDesc(eq(10L), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(last));

        var list = chatService.listConversations(userId);

        assertEquals(1, list.size());
        assertEquals(10L, list.get(0).getConversationId());
        assertEquals(2, list.get(0).getUnreadCount());
    }
}
