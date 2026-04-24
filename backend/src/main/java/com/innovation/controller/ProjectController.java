package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.AuditDTO;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.entity.Project;
import com.innovation.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目管理")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @ApiOperation("创建项目")
    @PostMapping
    public Result<Project> createProject(@Validated @RequestBody ProjectCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(projectService.createProject(dto, userId));
    }

    @ApiOperation("获取项目列表")
    @GetMapping
    public Result<PageResult<Project>> listProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer collegeId,
            @RequestParam(required = false) Integer applyYear,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Integer leaderId,
            @RequestParam(required = false) Integer teacherId) {
        IPage<Project> projectPage = projectService.listProjects(page, size, status, collegeId, applyYear, projectName, leaderId, teacherId);
        return Result.success(new PageResult<>(projectPage.getTotal(), projectPage.getRecords()));
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/{id}")
    public Result<Project> getProject(@PathVariable Integer id) {
        return Result.success(projectService.getProjectDetail(id));
    }

    @ApiOperation("提交项目")
    @PostMapping("/{id}/submit")
    public Result<Void> submitProject(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        projectService.submitProject(id, userId);
        return Result.success();
    }

    @ApiOperation("导师审核")
    @PostMapping("/teacher-audit")
    public Result<Void> teacherAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        if ("pass".equals(dto.getResult())) {
            projectService.updateProjectStatus(dto.getProjectId(), "wait_college_review", userId);
        } else {
            projectService.updateProjectStatus(dto.getProjectId(), "rejected", userId);
        }
        return Result.success();
    }

    @ApiOperation("学院终审")
    @PostMapping("/college-audit")
    public Result<Void> collegeAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        if ("pass".equals(dto.getResult())) {
            projectService.updateProjectStatus(dto.getProjectId(), "wait_school_review", userId);
        } else {
            projectService.updateProjectStatus(dto.getProjectId(), "rejected", userId);
        }
        return Result.success();
    }

    @ApiOperation("学校终审")
    @PostMapping("/school-audit")
    public Result<Void> schoolAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        if ("pass".equals(dto.getResult())) {
            projectService.updateProjectStatus(dto.getProjectId(), "approved", userId);
        } else {
            projectService.updateProjectStatus(dto.getProjectId(), "rejected", userId);
        }
        return Result.success();
    }

    @ApiOperation("更新项目")
    @PutMapping("/{id}")
    public Result<Void> updateProject(@PathVariable Integer id, @RequestBody Project project) {
        project.setProjectId(id);
        projectService.updateById(project);
        return Result.success();
    }

    @ApiOperation("删除项目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(@PathVariable Integer id) {
        projectService.removeById(id);
        return Result.success();
    }
}
