package com.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.AuditDTO;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectMember;
import com.innovation.entity.User;
import com.innovation.mapper.ProjectMemberMapper;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ProjectService;
import com.innovation.service.UserService;
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
    private UserService userService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        String role = extractRole(auth);

        Integer memberUserId = null;
        Integer filterTeacherId = teacherId;
        Integer filterCollegeId = collegeId;

        // 根据角色自动注入权限过滤
        switch (role) {
            case "student":
                // 学生只能看到自己是负责人或成员的项目
                memberUserId = userId;
                break;
            case "teacher":
                // 老师默认看到自己指导的项目，也可按其他条件筛选
                if (filterTeacherId == null) {
                    filterTeacherId = userId;
                }
                break;
            case "college_admin":
                // 院管理员默认看本院项目，允许手动选择其他学院
                if (filterCollegeId == null) {
                    User admin = userService.getById(userId);
                    if (admin != null && admin.getCollegeId() != null) {
                        filterCollegeId = admin.getCollegeId();
                    }
                }
                break;
            case "school_admin":
                // 校管理员可看所有项目
                break;
            default:
                break;
        }

        IPage<Project> projectPage = projectService.listProjects(page, size, status, filterCollegeId,
                applyYear, projectName, leaderId, filterTeacherId, memberUserId);
        return Result.success(new PageResult<>(projectPage.getTotal(), projectPage.getRecords()));
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/{id}")
    public Result<Project> getProject(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = extractRole(auth);
        Integer userId = (Integer) auth.getPrincipal();

        Project project = projectService.getProjectDetail(id);
        if (project == null) {
            return Result.success(null);
        }

        // 学生只能查看自己的项目（负责人或成员）
        if ("student".equals(role)) {
            if (!project.getLeaderId().equals(userId) && !isProjectMember(id, userId)) {
                return Result.error(403, "无权查看该项目");
            }
        }

        return Result.success(project);
    }

    @ApiOperation("提交项目")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('student','teacher')")
    public Result<Void> submitProject(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Project project = projectService.getById(id);
        projectService.submitProject(id, userId);
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = extractRole(auth);
        Integer userId = (Integer) auth.getPrincipal();

        // 学生只能修改自己的项目
        if ("student".equals(role)) {
            Project existing = projectService.getById(id);
            if (existing == null || (!existing.getLeaderId().equals(userId) && !isProjectMember(id, userId))) {
                return Result.error(403, "无权修改该项目");
            }
        }

        project.setProjectId(id);
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

    private String extractRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("");
    }

    private boolean isProjectMember(Integer projectId, Integer userId) {
        return projectMemberMapper.selectCount(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId)) > 0;
    }
}
