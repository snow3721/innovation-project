package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectCollegeAudit;
import com.innovation.entity.ProjectMember;
import com.innovation.entity.ProjectSchoolAudit;
import com.innovation.entity.ProjectTeacherAudit;
import com.innovation.entity.User;
import com.innovation.mapper.ProjectCollegeAuditMapper;
import com.innovation.mapper.ProjectMapper;
import com.innovation.mapper.ProjectMemberMapper;
import com.innovation.mapper.ProjectSchoolAuditMapper;
import com.innovation.mapper.ProjectTeacherAuditMapper;
import com.innovation.service.ProjectService;
import com.innovation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ProjectTeacherAuditMapper teacherAuditMapper;

    @Autowired
    private ProjectCollegeAuditMapper collegeAuditMapper;

    @Autowired
    private ProjectSchoolAuditMapper schoolAuditMapper;

    @Autowired
    private UserService userService;

    private static final Map<String, List<String>> STATE_FLOW = Map.ofEntries(
            Map.entry("draft", Arrays.asList("wait_teacher_audit")),
            Map.entry("wait_teacher_audit", Arrays.asList("draft", "wait_college_assign", "rejected")),
            Map.entry("wait_college_assign", Arrays.asList("wait_college_review")),
            Map.entry("wait_college_review", Arrays.asList("wait_college_audit")),
            Map.entry("wait_college_audit", Arrays.asList("wait_school_assign", "rejected")),
            Map.entry("wait_school_assign", Arrays.asList("wait_school_review")),
            Map.entry("wait_school_review", Arrays.asList("wait_school_audit")),
            Map.entry("wait_school_audit", Arrays.asList("approved", "rejected")),
            Map.entry("approved", Arrays.asList("running")),
            Map.entry("running", Arrays.asList("mid_checking")),
            Map.entry("mid_checking", Arrays.asList("running", "conclude_apply")),
            Map.entry("conclude_apply", Arrays.asList("concluded", "rejected")),
            Map.entry("rejected", Arrays.asList("draft"))
    );

    @Override
    @Transactional
    public Project createProject(ProjectCreateDTO dto, Integer userId) {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setCatId(dto.getCatId());
        project.setCollegeId(dto.getCollegeId());
        project.setLeaderId(userId);
        project.setTotalBudget(dto.getTotalBudget());
        project.setApplyYear(LocalDate.now().getYear());
        project.setStatus("draft");
        if (dto.getTeacherId() != null) {
            project.setTeacherId(dto.getTeacherId());
        }
        if (dto.getStartTime() != null) {
            project.setStartTime(LocalDate.parse(dto.getStartTime()));
        }
        if (dto.getEndTime() != null) {
            project.setEndTime(LocalDate.parse(dto.getEndTime()));
        }
        save(project);

        // 创建者自动作为leader成员
        ProjectMember leaderMember = new ProjectMember();
        leaderMember.setProjectId(project.getProjectId());
        leaderMember.setUserId(userId);
        leaderMember.setRole("leader");
        projectMemberMapper.insert(leaderMember);

        if (dto.getMembers() != null) {
            for (ProjectCreateDTO.MemberDTO memberDTO : dto.getMembers()) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getProjectId());
                member.setUserId(memberDTO.getUserId());
                member.setRole("normal");
                projectMemberMapper.insert(member);
            }
        }
        return project;
    }

    @Override
    public IPage<Project> listProjects(int page, int size, String status, Integer collegeId,
                                       Integer applyYear, String projectName, Integer leaderId,
                                       Integer teacherId, Integer memberUserId) {
        return baseMapper.selectProjectPage(new Page<>(page, size), status, collegeId, applyYear, projectName, leaderId, teacherId, memberUserId);
    }

    @Override
    public Project getProjectDetail(Integer projectId) {
        Project project = getById(projectId);
        if (project != null) {
            User leader = userService.getById(project.getLeaderId());
            if (leader != null) {
                project.setLeaderName(leader.getRealName());
            }
            if (project.getTeacherId() != null) {
                User teacher = userService.getById(project.getTeacherId());
                if (teacher != null) {
                    project.setTeacherName(teacher.getRealName());
                }
            }
            project.setStatusText(getStatusText(project.getStatus()));
        }
        return project;
    }

    @Override
    @Transactional
    public void submitProject(Integer projectId, Integer userId) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!project.getLeaderId().equals(userId)) {
            throw new RuntimeException("仅项目负责人可提交项目");
        }
        if (!"draft".equals(project.getStatus()) && !"rejected".equals(project.getStatus())) {
            throw new RuntimeException("仅草稿或被驳回的项目可提交");
        }
        if (project.getTeacherId() == null) {
            throw new RuntimeException("请先选择指导老师后再提交");
        }
        project.setStatus("wait_teacher_audit");
        boolean updated = updateById(project);
        if (!updated) {
            throw new RuntimeException("提交失败，项目数据已被其他用户修改，请刷新后重试");
        }
    }

    @Override
    public void updateProjectStatus(Integer projectId, String targetStatus, Integer operatorId) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        List<String> allowedStatuses = STATE_FLOW.get(project.getStatus());
        if (allowedStatuses == null || !allowedStatuses.contains(targetStatus)) {
            throw new RuntimeException("非法的状态流转: " + project.getStatus() + " -> " + targetStatus);
        }
        project.setStatus(targetStatus);
        boolean updated = updateById(project);
        if (!updated) {
            throw new RuntimeException("状态更新失败，项目数据已被其他用户修改，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void teacherAudit(Integer projectId, String result, Integer teacherId, String opinion) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"wait_teacher_audit".equals(project.getStatus())) {
            throw new RuntimeException("项目当前状态不是待导师审核");
        }
        if (!teacherId.equals(project.getTeacherId())) {
            throw new RuntimeException("仅该项目指导老师可审核");
        }
        // 写入审核记录
        ProjectTeacherAudit audit = new ProjectTeacherAudit();
        audit.setProjectId(projectId);
        audit.setTeacherId(teacherId);
        audit.setResult(result);
        teacherAuditMapper.insert(audit);
        // 更新状态
        if ("pass".equals(result)) {
            project.setStatus("wait_college_assign");
        } else {
            project.setStatus("rejected");
        }
        boolean updated = updateById(project);
        if (!updated) {
            throw new RuntimeException("审核失败，项目数据已被其他用户修改，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void collegeAudit(Integer projectId, String result, Integer adminId, String opinion) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"wait_college_audit".equals(project.getStatus())) {
            throw new RuntimeException("项目当前状态不是待院级终审");
        }
        // 写入审核记录
        ProjectCollegeAudit audit = new ProjectCollegeAudit();
        audit.setProjectId(projectId);
        audit.setAdminId(adminId);
        audit.setResult(result);
        collegeAuditMapper.insert(audit);
        // 更新状态
        if ("pass".equals(result)) {
            project.setStatus("wait_school_assign");
        } else {
            project.setStatus("rejected");
        }
        boolean updated = updateById(project);
        if (!updated) {
            throw new RuntimeException("审核失败，项目数据已被其他用户修改，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void schoolAudit(Integer projectId, String result, Integer adminId, String opinion) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!"wait_school_audit".equals(project.getStatus())) {
            throw new RuntimeException("项目当前状态不是待校级终审");
        }
        // 写入审核记录
        ProjectSchoolAudit audit = new ProjectSchoolAudit();
        audit.setProjectId(projectId);
        audit.setAdminId(adminId);
        audit.setResult(result);
        schoolAuditMapper.insert(audit);
        // 更新状态
        if ("pass".equals(result)) {
            project.setStatus("approved");
        } else {
            project.setStatus("rejected");
        }
        boolean updated = updateById(project);
        if (!updated) {
            throw new RuntimeException("审核失败，项目数据已被其他用户修改，请刷新后重试");
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "draft": return "草稿";
            case "wait_teacher_audit": return "待导师审核";
            case "wait_college_assign": return "待院级分配";
            case "wait_college_review": return "待院级评审";
            case "wait_college_audit": return "待院级终审";
            case "wait_school_assign": return "待校级分配";
            case "wait_school_review": return "待校级评审";
            case "wait_school_audit": return "待校级终审";
            case "approved": return "已立项";
            case "rejected": return "已驳回";
            case "running": return "运行中";
            case "mid_checking": return "中期检查";
            case "conclude_apply": return "待结题";
            case "concluded": return "已结题";
            default: return status;
        }
    }
}
