package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectMidCheck;
import com.innovation.mapper.ProjectMidCheckMapper;
import com.innovation.service.ProjectMidCheckService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectMidCheckServiceImpl extends ServiceImpl<ProjectMidCheckMapper, ProjectMidCheck>
        implements ProjectMidCheckService {

    @Autowired
    private ProjectService projectService;

    @Override
    @Transactional
    public ProjectMidCheck submitMidCheck(Integer projectId, Integer userId) {
        Project project = projectService.getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"running".equals(project.getStatus()) && !"mid_checking".equals(project.getStatus())) {
            throw new RuntimeException("当前项目状态不允许提交中期检查，项目状态：" + project.getStatus());
        }

        // 检查是否已存在中期检查记录
        ProjectMidCheck existing = getOne(new LambdaQueryWrapper<ProjectMidCheck>()
                .eq(ProjectMidCheck::getProjectId, projectId));
        if (existing != null && !"reject".equals(existing.getStatus())) {
            throw new RuntimeException("该项目已提交过中期检查");
        }

        ProjectMidCheck midCheck;
        if (existing != null && "reject".equals(existing.getStatus())) {
            // 驳回后可重新提交
            existing.setStatus("waiting");
            updateById(existing);
            midCheck = existing;
        } else {
            midCheck = new ProjectMidCheck();
            midCheck.setProjectId(projectId);
            midCheck.setStatus("waiting");
            save(midCheck);
        }

        // 推进项目状态为中期检查中
        if ("running".equals(project.getStatus())) {
            projectService.updateProjectStatus(projectId, "mid_checking", null);
        }

        return midCheck;
    }

    @Override
    @Transactional
    public ProjectMidCheck auditMidCheck(Integer midId, String result, Integer adminId) {
        ProjectMidCheck midCheck = getById(midId);
        if (midCheck == null) {
            throw new RuntimeException("中期检查记录不存在");
        }
        if (!"waiting".equals(midCheck.getStatus())) {
            throw new RuntimeException("当前中期检查状态不允许审核");
        }

        midCheck.setStatus(result);
        updateById(midCheck);

        // 审核通过后项目回到运行中状态，可继续申请结题
        // 审核驳回则项目也回到运行中状态，可修改后重新提交
        Project project = projectService.getById(midCheck.getProjectId());
        if (project != null && "mid_checking".equals(project.getStatus())) {
            if ("pass".equals(result)) {
                projectService.updateProjectStatus(midCheck.getProjectId(), "running", null);
            } else {
                projectService.updateProjectStatus(midCheck.getProjectId(), "running", null);
            }
        }

        return midCheck;
    }

    @Override
    public IPage<ProjectMidCheck> listMidChecks(int page, int size, String status, Integer projectId) {
        LambdaQueryWrapper<ProjectMidCheck> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(ProjectMidCheck::getStatus, status);
        }
        if (projectId != null) {
            wrapper.eq(ProjectMidCheck::getProjectId, projectId);
        }
        wrapper.orderByDesc(ProjectMidCheck::getSubmitTime);
        return page(new Page<>(page, size), wrapper);
    }
}
