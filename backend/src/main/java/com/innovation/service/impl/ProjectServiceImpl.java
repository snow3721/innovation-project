package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.ProjectCreateDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectMember;
import com.innovation.entity.User;
import com.innovation.mapper.ProjectMapper;
import com.innovation.mapper.ProjectMemberMapper;
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
    private UserService userService;

    private static final Map<String, List<String>> STATE_FLOW = Map.of(
            "draft", Arrays.asList("wait_teacher_audit"),
            "wait_teacher_audit", Arrays.asList("draft", "wait_college_review", "rejected"),
            "wait_college_review", Arrays.asList("wait_college_audit"),
            "wait_college_audit", Arrays.asList("wait_school_review", "rejected"),
            "wait_school_review", Arrays.asList("wait_school_audit"),
            "wait_school_audit", Arrays.asList("approved", "rejected"),
            "approved", Arrays.asList("running"),
            "running", Arrays.asList("mid_checking"),
            "mid_checking", Arrays.asList("running", "conclude_apply"),
            "conclude_apply", Arrays.asList("concluded", "rejected")
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
        if (dto.getStartTime() != null) {
            project.setStartTime(LocalDate.parse(dto.getStartTime()));
        }
        if (dto.getEndTime() != null) {
            project.setEndTime(LocalDate.parse(dto.getEndTime()));
        }
        save(project);

        if (dto.getMembers() != null) {
            for (ProjectCreateDTO.MemberDTO memberDTO : dto.getMembers()) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getProjectId());
                member.setUserId(memberDTO.getUserId());
                member.setRole(memberDTO.getRole());
                projectMemberMapper.insert(member);
            }
        }
        return project;
    }

    @Override
    public IPage<Project> listProjects(int page, int size, String status, Integer collegeId,
                                       Integer applyYear, String projectName, Integer leaderId, Integer teacherId) {
        return baseMapper.selectProjectPage(new Page<>(page, size), status, collegeId, applyYear, projectName, leaderId, teacherId);
    }

    @Override
    public Project getProjectDetail(Integer projectId) {
        Project project = getById(projectId);
        if (project != null) {
            User leader = userService.getById(project.getLeaderId());
            if (leader != null) {
                project.setLeaderName(leader.getRealName());
            }
            User teacher = userService.getById(project.getTeacherId());
            if (teacher != null) {
                project.setTeacherName(teacher.getRealName());
            }
            project.setStatusText(getStatusText(project.getStatus()));
        }
        return project;
    }

    @Override
    public void submitProject(Integer projectId, Integer userId) {
        Project project = getById(projectId);
        if (project != null && project.getLeaderId().equals(userId)) {
            project.setStatus("wait_teacher_audit");
            updateById(project);
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
        updateById(project);
    }

    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "draft": return "草稿";
            case "wait_teacher_audit": return "待导师审核";
            case "wait_college_review": return "待院级评审";
            case "wait_college_audit": return "待院级终审";
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
