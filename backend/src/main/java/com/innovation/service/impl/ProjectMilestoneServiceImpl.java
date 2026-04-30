package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectMilestone;
import com.innovation.mapper.ProjectMilestoneMapper;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ProjectMilestoneService;
import com.innovation.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProjectMilestoneServiceImpl extends ServiceImpl<ProjectMilestoneMapper, ProjectMilestone>
        implements ProjectMilestoneService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MessageProducer messageProducer;

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
        List<ProjectMilestone> milestones = list(wrapper);

        for (ProjectMilestone m : milestones) {
            long daysLeft = ChronoUnit.DAYS.between(today, m.getPlanTime());
            if (daysLeft < 0) {
                update(new LambdaUpdateWrapper<ProjectMilestone>()
                        .eq(ProjectMilestone::getMilestoneId, m.getMilestoneId())
                        .set(ProjectMilestone::getStatus, "overdue")
                        .set(ProjectMilestone::getIsWarning, 1));
                sendMilestoneNotification(m, "已逾期", (int) Math.abs(daysLeft));
            } else if (daysLeft <= 3) {
                update(new LambdaUpdateWrapper<ProjectMilestone>()
                        .eq(ProjectMilestone::getMilestoneId, m.getMilestoneId())
                        .set(ProjectMilestone::getIsWarning, 1));
                sendMilestoneNotification(m, "即将到期", (int) daysLeft);
            }
        }
    }

    private void sendMilestoneNotification(ProjectMilestone milestone, String statusText, int days) {
        Project project = projectService.getById(milestone.getProjectId());
        if (project != null && project.getLeaderId() != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setTitle("里程碑" + statusText);
            msg.setContent("项目「" + project.getProjectName() + "」的里程碑「" + milestone.getMilestoneName()
                    + "」" + statusText + "（" + days + "天），计划时间：" + milestone.getPlanTime() + "。");
            msg.setRelationId(milestone.getProjectId());
            messageProducer.sendMilestoneMessage(msg);
        }
    }
}
