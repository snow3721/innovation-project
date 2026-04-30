package com.innovation.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageSendDTO {

    @NotNull(message = "接收者ID不能为空")
    private Integer receiverId;

    private Integer senderId;

    @NotBlank(message = "消息标题不能为空")
    private String title;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    /** system/audit/review/milestone/achievement */
    private String type;

    private Integer relationId;
}
