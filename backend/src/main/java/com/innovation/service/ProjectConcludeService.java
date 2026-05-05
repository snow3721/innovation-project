package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.ProjectConclude;

public interface ProjectConcludeService extends IService<ProjectConclude> {

    ProjectConclude submitConclude(Integer projectId, Integer userId);

    ProjectConclude auditConclude(Integer concludeId, String result, Integer adminId);

    IPage<ProjectConclude> listConcludes(int page, int size, String status, Integer projectId);
}
