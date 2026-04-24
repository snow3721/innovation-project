package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.ExpertAssignment;
import com.innovation.mapper.ExpertAssignmentMapper;
import com.innovation.service.ExpertAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ExpertAssignmentServiceImpl extends ServiceImpl<ExpertAssignmentMapper, ExpertAssignment>
        implements ExpertAssignmentService {

    @Override
    public IPage<ExpertAssignment> listAssignments(int page, int size, Integer projectId, String stage) {
        LambdaQueryWrapper<ExpertAssignment> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ExpertAssignment::getProjectId, projectId);
        }
        if (StringUtils.hasText(stage)) {
            wrapper.eq(ExpertAssignment::getStage, stage);
        }
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public void assignExpert(Integer projectId, Integer expertId, String stage) {
        ExpertAssignment assignment = new ExpertAssignment();
        assignment.setProjectId(projectId);
        assignment.setExpertId(expertId);
        assignment.setStage(stage);
        save(assignment);
    }
}
