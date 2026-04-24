package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.ExpertAssignment;

public interface ExpertAssignmentService extends IService<ExpertAssignment> {

    IPage<ExpertAssignment> listAssignments(int page, int size, Integer projectId, String stage);

    void assignExpert(Integer projectId, Integer expertId, String stage);
}
