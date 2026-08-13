package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.innovation.dto.ChatMessageVO;
import com.innovation.dto.ConversationVO;
import com.innovation.entity.ChatMessage;
import com.innovation.entity.Conversation;
import com.innovation.entity.ConversationParticipant;
import com.innovation.entity.User;
import com.innovation.mapper.ConversationMapper;
import com.innovation.mapper.ConversationParticipantMapper;
import com.innovation.mapper.UserMapper;
import com.innovation.repository.ChatMessageRepository;
import com.innovation.service.ChatService;
import com.innovation.util.OnlineStatusManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ConversationMapper conversationMapper;
    private final ConversationParticipantMapper participantMapper;
    private final UserMapper userMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final OnlineStatusManager onlineStatusManager;

    public ChatServiceImpl(ConversationMapper conversationMapper,
                           ConversationParticipantMapper participantMapper,
                           UserMapper userMapper,
                           ChatMessageRepository chatMessageRepository,
                           OnlineStatusManager onlineStatusManager) {
        this.conversationMapper = conversationMapper;
        this.participantMapper = participantMapper;
        this.userMapper = userMapper;
        this.chatMessageRepository = chatMessageRepository;
        this.onlineStatusManager = onlineStatusManager;
    }

    @Override
    public List<ConversationVO> listConversations(Integer userId) {
        List<ConversationParticipant> participants = participantMapper.findActiveByUserId(userId);
        List<ConversationVO> result = new ArrayList<>();
        for (ConversationParticipant participant : participants) {
            Long conversationId = participant.getConversationId();
            Integer otherUserId = participantMapper.findOtherParticipant(conversationId, userId);
            if (otherUserId == null) {
                continue;
            }
            User other = userMapper.selectById(otherUserId);
            ConversationVO vo = new ConversationVO();
            vo.setConversationId(conversationId);
            vo.setOtherUserId(otherUserId);
            vo.setOtherUserName(other != null ? other.getRealName() : "未知用户");
            vo.setUnreadCount(participant.getUnreadCount() == null ? 0 : participant.getUnreadCount());
            vo.setOnline(onlineStatusManager.isOnline(otherUserId));
            List<ChatMessage> lastMessages = chatMessageRepository
                    .findByConversationIdOrderBySendTimeDesc(conversationId, PageRequest.of(0, 1));
            if (!lastMessages.isEmpty()) {
                ChatMessage last = lastMessages.get(0);
                vo.setLastMessage(last.getContent());
                vo.setLastMessageTime(last.getSendTime());
            }
            result.add(vo);
        }
        result.sort(Comparator.comparing(ConversationVO::getLastMessageTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    @Override
    public List<ChatMessageVO> getMessages(Long conversationId, Integer userId, int page, int size) {
        if (!isParticipant(conversationId, userId)) {
            throw new RuntimeException("无权访问该会话");
        }
        List<ChatMessage> messages = chatMessageRepository
                .findByConversationIdOrderBySendTimeDesc(conversationId, PageRequest.of(page - 1, size));
        List<ChatMessageVO> result = messages.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(ChatMessageVO::getSendTime));
        return result;
    }

    @Override
    @Transactional
    public ChatMessageVO sendMessage(Integer senderId, Integer receiverId, String content) {
        if (senderId == null || receiverId == null || senderId.equals(receiverId)) {
            throw new RuntimeException("无效的会话对象");
        }
        Long conversationId = participantMapper.findCommonConversation(senderId, receiverId);
        if (conversationId == null) {
            conversationId = createConversation(senderId, receiverId);
        }
        ChatMessage message = new ChatMessage(conversationId, senderId, content, LocalDateTime.now());
        chatMessageRepository.save(message);
        incrementUnread(conversationId, receiverId);
        return toVO(message);
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId, Integer userId) {
        participantMapper.update(null, new LambdaUpdateWrapper<ConversationParticipant>()
                .eq(ConversationParticipant::getConversationId, conversationId)
                .eq(ConversationParticipant::getUserId, userId)
                .set(ConversationParticipant::getDeleted, 1));
    }

    @Override
    @Transactional
    public void markRead(Long conversationId, Integer userId) {
        participantMapper.update(null, new LambdaUpdateWrapper<ConversationParticipant>()
                .eq(ConversationParticipant::getConversationId, conversationId)
                .eq(ConversationParticipant::getUserId, userId)
                .set(ConversationParticipant::getUnreadCount, 0));
    }

    @Override
    public Integer getOtherParticipant(Long conversationId, Integer userId) {
        return participantMapper.findOtherParticipant(conversationId, userId);
    }

    private Long createConversation(Integer userA, Integer userB) {
        Conversation conversation = new Conversation();
        conversationMapper.insert(conversation);
        Long conversationId = conversation.getConversationId();

        ConversationParticipant pa = new ConversationParticipant();
        pa.setConversationId(conversationId);
        pa.setUserId(userA);
        pa.setUnreadCount(0);
        pa.setDeleted(0);
        participantMapper.insert(pa);

        ConversationParticipant pb = new ConversationParticipant();
        pb.setConversationId(conversationId);
        pb.setUserId(userB);
        pb.setUnreadCount(0);
        pb.setDeleted(0);
        participantMapper.insert(pb);

        return conversationId;
    }

    private void incrementUnread(Long conversationId, Integer userId) {
        ConversationParticipant participant = participantMapper.selectOne(
                new LambdaQueryWrapper<ConversationParticipant>()
                        .eq(ConversationParticipant::getConversationId, conversationId)
                        .eq(ConversationParticipant::getUserId, userId));
        if (participant != null) {
            participant.setUnreadCount((participant.getUnreadCount() == null ? 0 : participant.getUnreadCount()) + 1);
            participantMapper.updateById(participant);
        }
    }

    private boolean isParticipant(Long conversationId, Integer userId) {
        return participantMapper.selectCount(new LambdaQueryWrapper<ConversationParticipant>()
                .eq(ConversationParticipant::getConversationId, conversationId)
                .eq(ConversationParticipant::getUserId, userId)
                .eq(ConversationParticipant::getDeleted, 0)) > 0;
    }

    private ChatMessageVO toVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderId(message.getSenderId());
        vo.setContent(message.getContent());
        vo.setSendTime(message.getSendTime());
        return vo;
    }
}
