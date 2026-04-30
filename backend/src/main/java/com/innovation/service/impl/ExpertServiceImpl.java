package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.Expert;
import com.innovation.mapper.ExpertMapper;
import com.innovation.service.ExpertService;
import org.springframework.stereotype.Service;

@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {

    @Override
    public Expert getOrCreateByUserId(Integer userId, String realName) {
        Expert expert = getOne(new LambdaQueryWrapper<Expert>().eq(Expert::getUserId, userId));
        if (expert == null) {
            expert = new Expert();
            expert.setUserId(userId);
            expert.setRealName(realName != null ? realName : "评审人");
            expert.setUnit("本校");
            expert.setIsInner(1);
            expert.setStatus(1);
            save(expert);
        }
        return expert;
    }
}
