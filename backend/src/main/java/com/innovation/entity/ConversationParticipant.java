package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("conversation_participant")
public class ConversationParticipant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Integer userId;

    private Integer unreadCount;

    private Integer deleted;
}
