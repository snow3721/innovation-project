package com.innovation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.innovation.entity.ConversationParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConversationParticipantMapper extends BaseMapper<ConversationParticipant> {

    @Select("SELECT cp1.conversation_id FROM conversation_participant cp1 " +
            "JOIN conversation_participant cp2 ON cp1.conversation_id = cp2.conversation_id " +
            "WHERE cp1.user_id = #{userA} AND cp2.user_id = #{userB} " +
            "AND cp1.deleted = 0 AND cp2.deleted = 0 LIMIT 1")
    Long findCommonConversation(@Param("userA") Integer userA, @Param("userB") Integer userB);

    @Select("SELECT user_id FROM conversation_participant " +
            "WHERE conversation_id = #{conversationId} AND user_id != #{userId} AND deleted = 0 LIMIT 1")
    Integer findOtherParticipant(@Param("conversationId") Long conversationId, @Param("userId") Integer userId);

    @Select("SELECT * FROM conversation_participant " +
            "WHERE user_id = #{userId} AND deleted = 0")
    List<ConversationParticipant> findActiveByUserId(@Param("userId") Integer userId);
}
