package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectAchievement;
import com.innovation.service.ProjectAchievementService;
import com.innovation.service.ProjectService;
import com.innovation.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectAchievementService achievementService;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        long totalProjects = projectService.count();
        long approvedProjects = projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "approved").or()
                .eq(Project::getStatus, "running").or()
                .eq(Project::getStatus, "concluded"));
        long runningProjects = projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "running"));
        long totalAchievements = achievementService.count();

        result.put("totalProjects", totalProjects);
        result.put("approvedProjects", approvedProjects);
        result.put("runningProjects", runningProjects);
        result.put("totalAchievements", totalAchievements);
        result.put("approvalRate", totalProjects > 0 ? Math.round(approvedProjects * 100.0 / totalProjects) : 0);
        return result;
    }

    @Override
    public Map<String, Object> getByYear(Integer year) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            wrapper.eq(Project::getApplyYear, year);
        }
        long total = projectService.count(wrapper);
        long approved = projectService.count(new LambdaQueryWrapper<Project>()
                .eq(year != null, Project::getApplyYear, year)
                .ne(Project::getStatus, "draft")
                .ne(Project::getStatus, "rejected"));
        result.put("year", year);
        result.put("total", total);
        result.put("approved", approved);
        result.put("rate", total > 0 ? Math.round(approved * 100.0 / total) : 0);
        return result;
    }

    @Override
    public Map<String, Object> getByCollege(Integer collegeId) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (collegeId != null) {
            wrapper.eq(Project::getCollegeId, collegeId);
        }
        result.put("total", projectService.count(wrapper));
        result.put("achievements", achievementService.count());
        return result;
    }

    @Override
    public Map<String, Object> getByCategory() {
        Map<String, Object> result = new HashMap<>();
        result.put("patent", achievementService.count(new LambdaQueryWrapper<ProjectAchievement>().eq(ProjectAchievement::getType, "patent")));
        result.put("paper", achievementService.count(new LambdaQueryWrapper<ProjectAchievement>().eq(ProjectAchievement::getType, "paper")));
        result.put("software", achievementService.count(new LambdaQueryWrapper<ProjectAchievement>().eq(ProjectAchievement::getType, "software")));
        result.put("competition", achievementService.count(new LambdaQueryWrapper<ProjectAchievement>().eq(ProjectAchievement::getType, "competition")));
        result.put("business", achievementService.count(new LambdaQueryWrapper<ProjectAchievement>().eq(ProjectAchievement::getType, "business")));
        return result;
    }
}
