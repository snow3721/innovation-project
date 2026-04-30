package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.AchievementDTO;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectAchievement;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ProjectAchievementService;
import com.innovation.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "成果管理")
@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    @Autowired
    private ProjectAchievementService achievementService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ProjectService projectService;

    @ApiOperation("提交成果")
    @PostMapping
    @PreAuthorize("hasAnyRole('student','teacher')")
    public Result<ProjectAchievement> createAchievement(@Validated @RequestBody AchievementDTO dto) {
        ProjectAchievement achievement = achievementService.createAchievement(dto);
        // 通知项目负责人有新成果提交
        Project project = projectService.getById(dto.getProjectId());
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setTitle("新成果提交");
            msg.setContent("项目「" + project.getProjectName() + "」提交了新成果：「" + achievement.getName() + "」。");
            msg.setRelationId(dto.getProjectId());
            messageProducer.sendAchievementMessage(msg);
        }
        return Result.success(achievement);
    }

    @ApiOperation("获取成果列表")
    @GetMapping
    public Result<PageResult<ProjectAchievement>> listAchievements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        IPage<ProjectAchievement> achievementPage = achievementService.listAchievements(page, size, projectId, type, status);
        return Result.success(new PageResult<>(achievementPage.getTotal(), achievementPage.getRecords()));
    }

    @ApiOperation("获取成果详情")
    @GetMapping("/{id}")
    public Result<ProjectAchievement> getAchievement(@PathVariable Integer id) {
        return Result.success(achievementService.getById(id));
    }

    @ApiOperation("更新成果")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('student','teacher','college_admin','school_admin')")
    public Result<Void> updateAchievement(@PathVariable Integer id, @RequestBody ProjectAchievement achievement) {
        achievement.setAchievementId(id);
        achievementService.updateById(achievement);
        return Result.success();
    }

    @ApiOperation("删除成果")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Void> deleteAchievement(@PathVariable Integer id) {
        achievementService.removeById(id);
        return Result.success();
    }
}
