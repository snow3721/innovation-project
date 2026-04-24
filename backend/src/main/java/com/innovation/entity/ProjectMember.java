package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_member")
public class ProjectMember {

    @TableId(type = IdType.AUTO)
    private Integer memberId;

    private Integer projectId;

    private Integer userId;

    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String realName;
}
