package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.AuditDTO;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Project;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private MessageProducer messageProducer;

    @ApiOperation("创建项目")
    @PostMapping
    @PreAuthorize("hasAnyRole('student','teacher','college_admin','school_admin')")
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
    @PreAuthorize("hasAnyRole('student','teacher')")
    public Result<Void> submitProject(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Project project = projectService.getById(id);
        projectService.submitProject(id, userId);
        // 通知指导老师审核
        if (project != null && project.getTeacherId() != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getTeacherId());
            msg.setSenderId(userId);
            msg.setTitle("新项目待审核");
            msg.setContent("项目「" + project.getProjectName() + "」已提交，请尽快审核。");
            msg.setRelationId(id);
            messageProducer.sendAuditMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("导师审核")
    @PostMapping("/teacher-audit")
    @PreAuthorize("hasRole('teacher')")
    public Result<Void> teacherAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Project project = projectService.getById(dto.getProjectId());
        projectService.teacherAudit(dto.getProjectId(), dto.getResult(), userId, dto.getOpinion());
        // 通知项目负责人
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setSenderId(userId);
            msg.setTitle("导师审核结果");
            msg.setContent("项目「" + project.getProjectName() + "」导师审核" + ("pass".equals(dto.getResult()) ? "通过，待院级分配专家" : "未通过") + "。");
            msg.setRelationId(dto.getProjectId());
            messageProducer.sendAuditMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("学院终审")
    @PostMapping("/college-audit")
    @PreAuthorize("hasRole('college_admin')")
    public Result<Void> collegeAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Project project = projectService.getById(dto.getProjectId());
        projectService.collegeAudit(dto.getProjectId(), dto.getResult(), userId, dto.getOpinion());
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setSenderId(userId);
            msg.setTitle("学院审核结果");
            msg.setContent("项目「" + project.getProjectName() + "」学院审核" + ("pass".equals(dto.getResult()) ? "通过，待校级分配专家" : "未通过") + "。");
            msg.setRelationId(dto.getProjectId());
            messageProducer.sendAuditMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("学校终审")
    @PostMapping("/school-audit")
    @PreAuthorize("hasRole('school_admin')")
    public Result<Void> schoolAudit(@RequestBody AuditDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Project project = projectService.getById(dto.getProjectId());
        projectService.schoolAudit(dto.getProjectId(), dto.getResult(), userId, dto.getOpinion());
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setSenderId(userId);
            msg.setTitle("学校审核结果");
            msg.setContent("项目「" + project.getProjectName() + "」学校审核" + ("pass".equals(dto.getResult()) ? "通过，已立项！" : "未通过") + "。");
            msg.setRelationId(dto.getProjectId());
            messageProducer.sendAuditMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("更新项目")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('student','teacher','college_admin','school_admin')")
    public Result<Void> updateProject(@PathVariable Integer id, @RequestBody Project project) {
        project.setProjectId(id);
        // 防止通过更新接口篡改关键字段
        project.setStatus(null);
        project.setLeaderId(null);
        project.setCollegeId(null);
        project.setApplyYear(null);
        projectService.updateById(project);
        return Result.success();
    }

    @ApiOperation("删除项目")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Void> deleteProject(@PathVariable Integer id) {
        projectService.removeById(id);
        return Result.success();
    }
}
