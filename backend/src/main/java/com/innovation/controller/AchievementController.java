package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.AchievementDTO;
import com.innovation.entity.ProjectAchievement;
import com.innovation.service.ProjectAchievementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "成果管理")
@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    @Autowired
    private ProjectAchievementService achievementService;

    @ApiOperation("提交成果")
    @PostMapping
    public Result<ProjectAchievement> createAchievement(@Validated @RequestBody AchievementDTO dto) {
        return Result.success(achievementService.createAchievement(dto));
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
    public Result<Void> updateAchievement(@PathVariable Integer id, @RequestBody ProjectAchievement achievement) {
        achievement.setAchievementId(id);
        achievementService.updateById(achievement);
        return Result.success();
    }

    @ApiOperation("删除成果")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAchievement(@PathVariable Integer id) {
        achievementService.removeById(id);
        return Result.success();
    }
}
