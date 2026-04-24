package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.ProjectMilestone;
import com.innovation.mapper.ProjectMilestoneMapper;
import com.innovation.service.ProjectMilestoneService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ProjectMilestoneServiceImpl extends ServiceImpl<ProjectMilestoneMapper, ProjectMilestone>
        implements ProjectMilestoneService {

    @Override
    public IPage<ProjectMilestone> listMilestones(int page, int size, Integer projectId, String status) {
        LambdaQueryWrapper<ProjectMilestone> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ProjectMilestone::getProjectId, projectId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProjectMilestone::getStatus, status);
        }
        wrapper.orderByAsc(ProjectMilestone::getPlanTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkMilestoneWarnings() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ProjectMilestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(ProjectMilestone::getStatus, "finished");
        wrapper.isNotNull(ProjectMilestone::getPlanTime);
        java.util.List<ProjectMilestone> milestones = list(wrapper);

        for (ProjectMilestone m : milestones) {
            long daysLeft = ChronoUnit.DAYS.between(today, m.getPlanTime());
            if (daysLeft < 0) {
                update(new LambdaUpdateWrapper<ProjectMilestone>()
                        .eq(ProjectMilestone::getMilestoneId, m.getMilestoneId())
                        .set(ProjectMilestone::getStatus, "overdue")
                        .set(ProjectMilestone::getIsWarning, 1));
            } else if (daysLeft <= 3) {
                update(new LambdaUpdateWrapper<ProjectMilestone>()
                        .eq(ProjectMilestone::getMilestoneId, m.getMilestoneId())
                        .set(ProjectMilestone::getIsWarning, 1));
            }
        }
    }
}
