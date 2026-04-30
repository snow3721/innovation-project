package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectMilestone;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ProjectMilestoneService;
import com.innovation.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "里程碑管理")
@RestController
@RequestMapping("/api/v1/milestones")
public class MilestoneController {

    @Autowired
    private ProjectMilestoneService milestoneService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ProjectService projectService;

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
    @PreAuthorize("hasAnyRole('student','teacher','college_admin','school_admin')")
    public Result<Void> createMilestone(@RequestBody ProjectMilestone milestone) {
        milestoneService.save(milestone);
        // 通知项目负责人新增里程碑
        Project project = projectService.getById(milestone.getProjectId());
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setTitle("新增里程碑");
            msg.setContent("项目「" + project.getProjectName() + "」新增里程碑：「" + milestone.getMilestoneName() + "」，计划时间：" + milestone.getPlanTime() + "。");
            msg.setRelationId(milestone.getProjectId());
            messageProducer.sendMilestoneMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("更新里程碑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('student','teacher','college_admin','school_admin')")
    public Result<Void> updateMilestone(@PathVariable Integer id, @RequestBody ProjectMilestone milestone) {
        milestone.setMilestoneId(id);
        milestoneService.updateById(milestone);
        return Result.success();
    }

    @ApiOperation("删除里程碑")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Void> deleteMilestone(@PathVariable Integer id) {
        milestoneService.removeById(id);
        return Result.success();
    }
}
