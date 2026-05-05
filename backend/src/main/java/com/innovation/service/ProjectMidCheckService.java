package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.ProjectMidCheck;

public interface ProjectMidCheckService extends IService<ProjectMidCheck> {

    ProjectMidCheck submitMidCheck(Integer projectId, Integer userId);

    ProjectMidCheck auditMidCheck(Integer midId, String result, Integer adminId);

    IPage<ProjectMidCheck> listMidChecks(int page, int size, String status, Integer projectId);
}
