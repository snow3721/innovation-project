package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.entity.Project;

public interface ProjectService extends IService<Project> {

    Project createProject(ProjectCreateDTO dto, Integer userId);

    IPage<Project> listProjects(int page, int size, String status, Integer collegeId,
                                Integer applyYear, String projectName, Integer leaderId, Integer teacherId);

    Project getProjectDetail(Integer projectId);

    void submitProject(Integer projectId, Integer userId);

    void updateProjectStatus(Integer projectId, String targetStatus, Integer operatorId);
}
