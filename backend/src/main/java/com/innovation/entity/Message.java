package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long messageId;

    /** 接收者用户ID */
    private Integer receiverId;

    /** 发送者用户ID（系统消息为null） */
    private Integer senderId;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 消息类型: system/audit/review/milestone/achievement */
    private String type;

    /** 关联业务ID（项目ID等） */
    private Integer relationId;

    /** 是否已读: 0未读 1已读 */
    private Integer isRead;

    /** 已读时间 */
    private LocalDateTime readTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
