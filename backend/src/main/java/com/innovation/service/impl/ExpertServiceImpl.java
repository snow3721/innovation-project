package com.innovation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.Expert;
import com.innovation.mapper.ExpertMapper;
import com.innovation.service.ExpertService;
import org.springframework.stereotype.Service;

@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {
}
