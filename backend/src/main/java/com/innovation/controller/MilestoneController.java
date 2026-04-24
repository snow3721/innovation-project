package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.entity.ProjectMilestone;
import com.innovation.service.ProjectMilestoneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "里程碑管理")
@RestController
@RequestMapping("/api/v1/milestones")
public class MilestoneController {

    @Autowired
    private ProjectMilestoneService milestoneService;

    @ApiOperation("获取里程碑列表")
    @GetMapping
    public Result<PageResult<ProjectMilestone>> listMilestones(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String status) {
        IPage<ProjectMilestone> milestonePage = milestoneService.listMilestones(page, size, projectId, status);
        return Result.success(new PageResult<>(milestonePage.getTotal(), milestonePage.getRecords()));
    }

    @ApiOperation("创建里程碑")
    @PostMapping
    public Result<Void> createMilestone(@RequestBody ProjectMilestone milestone) {
        milestoneService.save(milestone);
        return Result.success();
    }

    @ApiOperation("更新里程碑")
    @PutMapping("/{id}")
    public Result<Void> updateMilestone(@PathVariable Integer id, @RequestBody ProjectMilestone milestone) {
        milestone.setMilestoneId(id);
        milestoneService.updateById(milestone);
        return Result.success();
    }

    @ApiOperation("删除里程碑")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMilestone(@PathVariable Integer id) {
        milestoneService.removeById(id);
        return Result.success();
    }
}
