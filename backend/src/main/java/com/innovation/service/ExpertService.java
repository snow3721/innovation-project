package com.innovation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.Expert;

public interface ExpertService extends IService<Expert> {

    /**
     * 根据userId获取Expert记录，若不存在则自动创建
     */
    Expert getOrCreateByUserId(Integer userId, String realName);
}
