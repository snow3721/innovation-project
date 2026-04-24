package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.ProjectMilestone;

public interface ProjectMilestoneService extends IService<ProjectMilestone> {

    IPage<ProjectMilestone> listMilestones(int page, int size, Integer projectId, String status);

    void checkMilestoneWarnings();
}
