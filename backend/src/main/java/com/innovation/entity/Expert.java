package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("expert")
public class Expert {

    @TableId(type = IdType.AUTO)
    private Integer expertId;

    private Integer userId;

    private String realName;

    private String unit;

    private String title;

    private String researchField;

    private Integer isInner;

    private Integer status;
}
