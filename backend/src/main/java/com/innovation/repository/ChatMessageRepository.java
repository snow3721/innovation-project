package com.innovation.repository;

import com.innovation.entity.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByConversationIdOrderBySendTimeDesc(Long conversationId, PageRequest pageRequest);

    List<ChatMessage> findByConversationIdOrderBySendTimeAsc(Long conversationId);
}
