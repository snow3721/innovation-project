package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.dto.ReviewScoreDTO;
import com.innovation.entity.ProjectReviewScore;

public interface ProjectReviewScoreService extends IService<ProjectReviewScore> {

    ProjectReviewScore submitScore(ReviewScoreDTO dto, Integer expertUserId);

    IPage<ProjectReviewScore> listScores(int page, int size, Integer projectId, String stage);
}
