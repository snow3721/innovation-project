package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.entity.Project;

public interface ProjectService extends IService<Project> {

    Project createProject(ProjectCreateDTO dto, Integer userId);

    IPage<Project> listProjects(int page, int size, String status, Integer collegeId,
                                Integer applyYear, String projectName, Integer leaderId,
                                Integer teacherId, Integer memberUserId);

    Project getProjectDetail(Integer projectId);

    void submitProject(Integer projectId, Integer userId);

    void updateProjectStatus(Integer projectId, String targetStatus, Integer operatorId);

    void teacherAudit(Integer projectId, String result, Integer teacherId, String opinion);

    void collegeAudit(Integer projectId, String result, Integer adminId, String opinion);

    void schoolAudit(Integer projectId, String result, Integer adminId, String opinion);
}
