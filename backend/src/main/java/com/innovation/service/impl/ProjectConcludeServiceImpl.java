package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectConclude;
import com.innovation.mapper.ProjectConcludeMapper;
import com.innovation.service.ProjectConcludeService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectConcludeServiceImpl extends ServiceImpl<ProjectConcludeMapper, ProjectConclude>
        implements ProjectConcludeService {

    @Autowired
    private ProjectService projectService;

    @Override
    @Transactional
    public ProjectConclude submitConclude(Integer projectId, Integer userId) {
        Project project = projectService.getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"running".equals(project.getStatus()) && !"mid_checking".equals(project.getStatus())) {
            throw new RuntimeException("当前项目状态不允许提交结题申请，项目状态：" + project.getStatus());
        }

        // 检查是否已存在结题记录
        ProjectConclude existing = getOne(new LambdaQueryWrapper<ProjectConclude>()
                .eq(ProjectConclude::getProjectId, projectId));
        if (existing != null && !"reject".equals(existing.getStatus())) {
            throw new RuntimeException("该项目已提交过结题申请");
        }

        ProjectConclude conclude;
        if (existing != null && "reject".equals(existing.getStatus())) {
            existing.setStatus("waiting");
            updateById(existing);
            conclude = existing;
        } else {
            conclude = new ProjectConclude();
            conclude.setProjectId(projectId);
            conclude.setStatus("waiting");
            save(conclude);
        }

        // 推进项目状态为待结题
        projectService.updateProjectStatus(projectId, "conclude_apply", null);

        return conclude;
    }

    @Override
    @Transactional
    public ProjectConclude auditConclude(Integer concludeId, String result, Integer adminId) {
        ProjectConclude conclude = getById(concludeId);
        if (conclude == null) {
            throw new RuntimeException("结题记录不存在");
        }
        if (!"waiting".equals(conclude.getStatus())) {
            throw new RuntimeException("当前结题申请状态不允许审核");
        }

        conclude.setStatus(result);
        updateById(conclude);

        // 更新项目状态
        Project project = projectService.getById(conclude.getProjectId());
        if (project != null) {
            if ("pass".equals(result)) {
                projectService.updateProjectStatus(conclude.getProjectId(), "concluded", null);
            } else {
                // 驳回则回到运行中状态
                projectService.updateProjectStatus(conclude.getProjectId(), "running", null);
            }
        }

        return conclude;
    }

    @Override
    public IPage<ProjectConclude> listConcludes(int page, int size, String status, Integer projectId) {
        LambdaQueryWrapper<ProjectConclude> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(ProjectConclude::getStatus, status);
        }
        if (projectId != null) {
            wrapper.eq(ProjectConclude::getProjectId, projectId);
        }
        wrapper.orderByDesc(ProjectConclude::getSubmitTime);
        return page(new Page<>(page, size), wrapper);
    }
}
