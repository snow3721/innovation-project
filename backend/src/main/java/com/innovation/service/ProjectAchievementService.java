package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.dto.AchievementDTO;
import com.innovation.entity.ProjectAchievement;

public interface ProjectAchievementService extends IService<ProjectAchievement> {

    ProjectAchievement createAchievement(AchievementDTO dto);

    IPage<ProjectAchievement> listAchievements(int page, int size, Integer projectId, String type, String status);
}
