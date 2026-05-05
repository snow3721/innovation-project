package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.ReviewScoreDTO;
import com.innovation.entity.Expert;
import com.innovation.entity.ExpertAssignment;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectReviewScore;
import com.innovation.mapper.ProjectReviewScoreMapper;
import com.innovation.service.ExpertAssignmentService;
import com.innovation.service.ExpertService;
import com.innovation.service.ProjectReviewScoreService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectReviewScoreServiceImpl extends ServiceImpl<ProjectReviewScoreMapper, ProjectReviewScore>
        implements ProjectReviewScoreService {

    @Autowired
    private ExpertService expertService;

    @Autowired
    private ProjectService projectService;

    @Lazy
    @Autowired
    private ExpertAssignmentService expertAssignmentService;

    @Override
    public ProjectReviewScore submitScore(ReviewScoreDTO dto, Integer expertUserId) {
        // 获取或自动创建Expert记录，支持所有非学生角色进行评审
        Expert expert = expertService.getOrCreateByUserId(expertUserId, null);

        // 校验：该专家必须被分配了该项目的该阶段评审任务
        long assignedCount = expertAssignmentService.count(new LambdaQueryWrapper<ExpertAssignment>()
                .eq(ExpertAssignment::getProjectId, dto.getProjectId())
                .eq(ExpertAssignment::getExpertId, expert.getExpertId())
                .eq(ExpertAssignment::getStage, dto.getStage()));
        if (assignedCount == 0) {
            throw new RuntimeException("您未被分配该项目的评审任务，无权打分");
        }

        // 检查是否已打分，防止重复打分
        long existCount = count(new LambdaQueryWrapper<ProjectReviewScore>()
                .eq(ProjectReviewScore::getProjectId, dto.getProjectId())
                .eq(ProjectReviewScore::getExpertId, expert.getExpertId())
                .eq(ProjectReviewScore::getReviewStage, dto.getStage()));
        if (existCount > 0) {
            throw new RuntimeException("您已对该项目进行过评审打分，不可重复提交");
        }

        ProjectReviewScore score = new ProjectReviewScore();
        score.setProjectId(dto.getProjectId());
        score.setExpertId(expert.getExpertId());
        score.setReviewStage(dto.getStage());
        score.setScoreInnovation(dto.getScoreInnovation());
        score.setScoreFeasibility(dto.getScoreFeasibility());
        score.setScoreTeam(dto.getScoreTeam());
        score.setScoreValue(dto.getScoreValue());
        score.setTotalScore(dto.getTotalScore());
        score.setOpinion(dto.getOpinion());
        save(score);

        checkAndAdvanceStage(dto.getProjectId(), dto.getStage());
        return score;
    }

    @Override
    public IPage<ProjectReviewScore> listScores(int page, int size, Integer projectId, String stage) {
        return baseMapper.selectScorePageWithDetail(new Page<>(page, size), projectId, stage);
    }

    private void checkAndAdvanceStage(Integer projectId, String stage) {
        // 查询该阶段已分配的专家总数
        long assignedCount = expertAssignmentService.count(new LambdaQueryWrapper<ExpertAssignment>()
                .eq(ExpertAssignment::getProjectId, projectId)
                .eq(ExpertAssignment::getStage, stage));

        // 查询该阶段已打分的专家数
        long scoredCount = count(new LambdaQueryWrapper<ProjectReviewScore>()
                .eq(ProjectReviewScore::getProjectId, projectId)
                .eq(ProjectReviewScore::getReviewStage, stage));

        // 所有分配的专家都打分完毕后，才推进到下一阶段
        if (assignedCount > 0 && scoredCount >= assignedCount) {
            if (stage.equals("college")) {
                projectService.updateProjectStatus(projectId, "wait_college_audit", null);
            } else if (stage.equals("school")) {
                projectService.updateProjectStatus(projectId, "wait_school_audit", null);
            }
        }
    }
}
