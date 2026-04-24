package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("project_category")
public class ProjectCategory {

    @TableId(type = IdType.AUTO)
    private Integer catId;

    private String catName;

    private String remark;
}
