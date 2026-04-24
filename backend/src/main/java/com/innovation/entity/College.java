package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("college")
public class College {

    @TableId(type = IdType.AUTO)
    private Integer collegeId;

    private String collegeName;

    private Integer sort;

    private Integer status;
}
