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
import com.innovation.mapper.ExpertAssignmentMapper;
import com.innovation.service.ExpertAssignmentService;
import com.innovation.service.ExpertService;
import com.innovation.service.ProjectReviewScoreService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpertAssignmentServiceImpl extends ServiceImpl<ExpertAssignmentMapper, ExpertAssignment>
        implements ExpertAssignmentService {

    @Autowired
    private ExpertService expertService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectReviewScoreService reviewScoreService;

    @Override
    public IPage<ExpertAssignment> listAssignments(int page, int size, Integer projectId, String stage) {
        LambdaQueryWrapper<ExpertAssignment> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ExpertAssignment::getProjectId, projectId);
        }
        if (StringUtils.hasText(stage)) {
            wrapper.eq(ExpertAssignment::getStage, stage);
        }
        return page(new Page<>(page, size), wrapper);
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
    public List<MyReviewTaskDTO> listMyReviewTasks(Integer expertUserId) {
        // 根据userId获取或创建专家记录，支持非expert角色查看评审任务
        Expert expert = expertService.getOrCreateByUserId(expertUserId, null);

        // 查询该专家被分配的所有评审任务
        List<ExpertAssignment> assignments = list(new LambdaQueryWrapper<ExpertAssignment>()
                .eq(ExpertAssignment::getExpertId, expert.getExpertId())
                .orderByDesc(ExpertAssignment::getAssignTime));

        List<MyReviewTaskDTO> result = new ArrayList<>();
        for (ExpertAssignment assignment : assignments) {
            MyReviewTaskDTO dto = new MyReviewTaskDTO();
            dto.setAssignmentId(assignment.getAssignmentId());
            dto.setProjectId(assignment.getProjectId());
            dto.setStage(assignment.getStage());
            dto.setDeadline(assignment.getDeadline());
            dto.setAssignTime(assignment.getAssignTime());

            // 填充项目名称
            Project project = projectService.getById(assignment.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
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
        return result;
    }
}
