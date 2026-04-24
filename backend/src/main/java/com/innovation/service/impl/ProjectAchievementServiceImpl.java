package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.AchievementDTO;
import com.innovation.entity.ProjectAchievement;
import com.innovation.mapper.ProjectAchievementMapper;
import com.innovation.service.ProjectAchievementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class ProjectAchievementServiceImpl extends ServiceImpl<ProjectAchievementMapper, ProjectAchievement>
        implements ProjectAchievementService {

    @Override
    public ProjectAchievement createAchievement(AchievementDTO dto) {
        ProjectAchievement achievement = new ProjectAchievement();
        achievement.setProjectId(dto.getProjectId());
        achievement.setType(dto.getType());
        achievement.setName(dto.getName());
        achievement.setAchievementNo(dto.getAchievementNo());
        achievement.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getPublishTime())) {
            achievement.setPublishTime(LocalDate.parse(dto.getPublishTime()));
        }
        save(achievement);
        return achievement;
    }

    @Override
    public IPage<ProjectAchievement> listAchievements(int page, int size, Integer projectId, String type, String status) {
        LambdaQueryWrapper<ProjectAchievement> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ProjectAchievement::getProjectId, projectId);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(ProjectAchievement::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectAchievement::getStatus, status);
        }
        wrapper.orderByDesc(ProjectAchievement::getPublishTime);
        return page(new Page<>(page, size), wrapper);
    }
}
