package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.MyReviewTaskDTO;
import com.innovation.entity.Expert;
import com.innovation.entity.ExpertAssignment;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectReviewScore;
import com.innovation.entity.User;
import com.innovation.mapper.ExpertAssignmentMapper;
import com.innovation.service.ExpertAssignmentService;
import com.innovation.service.ExpertService;
import com.innovation.service.ProjectReviewScoreService;
import com.innovation.service.ProjectService;
import com.innovation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpertAssignmentServiceImpl extends ServiceImpl<ExpertAssignmentMapper, ExpertAssignment>
        implements ExpertAssignmentService {

    @Autowired
    private ExpertService expertService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectReviewScoreService reviewScoreService;

    @Override
    public IPage<ExpertAssignment> listAssignments(int page, int size, Integer projectId, String stage) {
        return baseMapper.selectAssignmentPageWithDetail(new Page<>(page, size), projectId, stage);
    }

    @Override
    public void assignExpert(Integer projectId, Integer expertId, String stage, java.time.LocalDateTime deadline) {
        ExpertAssignment assignment = new ExpertAssignment();
        assignment.setProjectId(projectId);
        assignment.setExpertId(expertId);
        assignment.setStage(stage);
        assignment.setDeadline(deadline);
        save(assignment);

        // 分配专家后自动推进项目状态：wait_college_assign → wait_college_review
        // wait_school_assign → wait_school_review
        Project project = projectService.getById(projectId);
        if (project != null) {
            if ("college".equals(stage) && "wait_college_assign".equals(project.getStatus())) {
                projectService.updateProjectStatus(projectId, "wait_college_review", null);
            } else if ("school".equals(stage) && "wait_school_assign".equals(project.getStatus())) {
                projectService.updateProjectStatus(projectId, "wait_school_review", null);
            }
        }
    }

    @Override
    public List<MyReviewTaskDTO> listMyReviewTasks(Integer expertUserId, String role) {
        List<MyReviewTaskDTO> result = new ArrayList<>();

        // ===== 1. 评审打分任务（来自 expert_assignment） =====
        Expert expert = expertService.getOrCreateByUserId(expertUserId, null);

        List<ExpertAssignment> assignments = list(new LambdaQueryWrapper<ExpertAssignment>()
                .eq(ExpertAssignment::getExpertId, expert.getExpertId())
                .orderByDesc(ExpertAssignment::getAssignTime));

        for (ExpertAssignment assignment : assignments) {
            MyReviewTaskDTO dto = new MyReviewTaskDTO();
            dto.setType("review");
            dto.setAssignmentId(assignment.getAssignmentId());
            dto.setProjectId(assignment.getProjectId());
            dto.setStage(assignment.getStage());
            dto.setDeadline(assignment.getDeadline());
            dto.setAssignTime(assignment.getAssignTime());

            // 填充项目名称和状态
            Project project = projectService.getById(assignment.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
                dto.setProjectStatus(project.getStatus());
            }

            // 检查该专家是否已对该项目该阶段打分
            long scoredCount = reviewScoreService.count(new LambdaQueryWrapper<ProjectReviewScore>()
                    .eq(ProjectReviewScore::getProjectId, assignment.getProjectId())
                    .eq(ProjectReviewScore::getExpertId, expert.getExpertId())
                    .eq(ProjectReviewScore::getReviewStage, assignment.getStage()));
            dto.setScored(scoredCount > 0);
            if (scoredCount > 0) {
                ProjectReviewScore score = reviewScoreService.getOne(new LambdaQueryWrapper<ProjectReviewScore>()
                        .eq(ProjectReviewScore::getProjectId, assignment.getProjectId())
                        .eq(ProjectReviewScore::getExpertId, expert.getExpertId())
                        .eq(ProjectReviewScore::getReviewStage, assignment.getStage()));
                if (score != null) {
                    dto.setScoreId(score.getScoreId());
                }
            }

            result.add(dto);
        }

        // ===== 2. 审核任务（根据角色和项目状态） =====
        // 导师：查看以自己为指导老师且状态为 wait_teacher_audit 的项目
        if ("teacher".equals(role)) {
            List<Project> teacherProjects = projectService.list(new LambdaQueryWrapper<Project>()
                    .eq(Project::getTeacherId, expertUserId)
                    .eq(Project::getStatus, "wait_teacher_audit"));
            for (Project project : teacherProjects) {
                MyReviewTaskDTO dto = new MyReviewTaskDTO();
                dto.setType("audit");
                dto.setAuditType("teacher_audit");
                dto.setProjectId(project.getProjectId());
                dto.setProjectName(project.getProjectName());
                dto.setProjectStatus(project.getStatus());
                dto.setScored(false);
                result.add(dto);
            }
        }

        // 院管理员：查看本院状态为 wait_college_audit 的项目
        if ("college_admin".equals(role)) {
            // 获取管理员所属学院
            User admin = userService.getById(expertUserId);
            if (admin != null && admin.getCollegeId() != null) {
                List<Project> collegeProjects = projectService.list(new LambdaQueryWrapper<Project>()
                        .eq(Project::getCollegeId, admin.getCollegeId())
                        .eq(Project::getStatus, "wait_college_audit"));
                for (Project project : collegeProjects) {
                    MyReviewTaskDTO dto = new MyReviewTaskDTO();
                    dto.setType("audit");
                    dto.setAuditType("college_audit");
                    dto.setProjectId(project.getProjectId());
                    dto.setProjectName(project.getProjectName());
                    dto.setProjectStatus(project.getStatus());
                    dto.setScored(false);
                    result.add(dto);
                }
            }
        }

        // 校管理员：查看所有状态为 wait_school_audit 的项目
        if ("school_admin".equals(role)) {
            List<Project> schoolProjects = projectService.list(new LambdaQueryWrapper<Project>()
                    .eq(Project::getStatus, "wait_school_audit"));
            for (Project project : schoolProjects) {
                MyReviewTaskDTO dto = new MyReviewTaskDTO();
                dto.setType("audit");
                dto.setAuditType("school_audit");
                dto.setProjectId(project.getProjectId());
                dto.setProjectName(project.getProjectName());
                dto.setProjectStatus(project.getStatus());
                dto.setScored(false);
                result.add(dto);
            }
        }

        return result;
    }
}
