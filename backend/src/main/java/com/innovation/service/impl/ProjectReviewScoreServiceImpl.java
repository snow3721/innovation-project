package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.ReviewScoreDTO;
import com.innovation.entity.Expert;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectReviewScore;
import com.innovation.mapper.ProjectReviewScoreMapper;
import com.innovation.service.ExpertService;
import com.innovation.service.ProjectReviewScoreService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectReviewScoreServiceImpl extends ServiceImpl<ProjectReviewScoreMapper, ProjectReviewScore>
        implements ProjectReviewScoreService {

    @Autowired
    private ExpertService expertService;

    @Autowired
    private ProjectService projectService;

    @Override
    public ProjectReviewScore submitScore(ReviewScoreDTO dto, Integer expertUserId) {
        Expert expert = expertService.getOne(new LambdaQueryWrapper<Expert>().eq(Expert::getUserId, expertUserId));
        if (expert == null) {
            throw new RuntimeException("专家信息不存在");
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
        save(score);

        checkAndAdvanceStage(dto.getProjectId(), dto.getStage());
        return score;
    }

    @Override
    public IPage<ProjectReviewScore> listScores(int page, int size, Integer projectId, String stage) {
        LambdaQueryWrapper<ProjectReviewScore> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ProjectReviewScore::getProjectId, projectId);
        }
        if (StringUtils.hasText(stage)) {
            wrapper.eq(ProjectReviewScore::getReviewStage, stage);
        }
        wrapper.orderByDesc(ProjectReviewScore::getScoreTime);
        return page(new Page<>(page, size), wrapper);
    }

    private void checkAndAdvanceStage(Integer projectId, String stage) {
        LambdaQueryWrapper<ProjectReviewScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectReviewScore::getProjectId, projectId);
        wrapper.eq(ProjectReviewScore::getReviewStage, stage);
        long scoredCount = count(wrapper);

        // Simplified: advance if at least one expert scored
        // In production, check against total assigned experts
        if (stage.equals("college")) {
            projectService.updateProjectStatus(projectId, "wait_college_audit", null);
        } else if (stage.equals("school")) {
            projectService.updateProjectStatus(projectId, "wait_school_audit", null);
        }
    }
}
