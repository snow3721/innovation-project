package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.MyReviewTaskDTO;
import com.innovation.dto.ReviewScoreDTO;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Expert;
import com.innovation.entity.ExpertAssignment;
import com.innovation.entity.Project;
import com.innovation.entity.ProjectReviewScore;
import com.innovation.mq.MessageProducer;
import com.innovation.service.ExpertAssignmentService;
import com.innovation.service.ExpertService;
import com.innovation.service.ProjectReviewScoreService;
import com.innovation.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "评审管理")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ProjectReviewScoreService reviewScoreService;

    @Autowired
    private ExpertAssignmentService expertAssignmentService;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ProjectService projectService;

    @ApiOperation("提交评分")
    @PostMapping("/scores")
    @PreAuthorize("hasAnyRole('expert','teacher','college_admin','school_admin')")
    public Result<ProjectReviewScore> submitScore(@Validated @RequestBody ReviewScoreDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        ProjectReviewScore score = reviewScoreService.submitScore(dto, userId);
        // 通知项目负责人有新的评审结果
        Project project = projectService.getById(dto.getProjectId());
        if (project != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(project.getLeaderId());
            msg.setSenderId(userId);
            msg.setTitle("评审打分通知");
            msg.setContent("项目「" + project.getProjectName() + "」收到新的评审打分，总分：" + dto.getTotalScore() + "。");
            msg.setRelationId(dto.getProjectId());
            messageProducer.sendReviewMessage(msg);
        }
        return Result.success(score);
    }

    @ApiOperation("获取评审打分列表")
    @GetMapping("/scores")
    public Result<PageResult<ProjectReviewScore>> listScores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String stage) {
        IPage<ProjectReviewScore> scorePage = reviewScoreService.listScores(page, size, projectId, stage);
        return Result.success(new PageResult<>(scorePage.getTotal(), scorePage.getRecords()));
    }

    @ApiOperation("分配专家")
    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Void> assignExpert(@RequestBody ExpertAssignment assignment) {
        expertAssignmentService.assignExpert(assignment.getProjectId(), assignment.getExpertId(), assignment.getStage(), assignment.getDeadline());
        // 通知专家有新的评审任务 - 通过expertId查找对应的userId
        Expert expert = expertService.getById(assignment.getExpertId());
        if (expert != null && expert.getUserId() != null) {
            MessageSendDTO msg = new MessageSendDTO();
            msg.setReceiverId(expert.getUserId());
            msg.setTitle("新评审任务");
            msg.setContent("您被分配了一个" + ("college".equals(assignment.getStage()) ? "院级" : "校级") + "评审任务，项目ID：" + assignment.getProjectId() + "，请尽快完成评审。");
            msg.setRelationId(assignment.getProjectId());
            messageProducer.sendReviewMessage(msg);
        }
        return Result.success();
    }

    @ApiOperation("获取专家分配列表")
    @GetMapping("/assignments")
    public Result<PageResult<ExpertAssignment>> listAssignments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String stage) {
        IPage<ExpertAssignment> assignmentPage = expertAssignmentService.listAssignments(page, size, projectId, stage);
        return Result.success(new PageResult<>(assignmentPage.getTotal(), assignmentPage.getRecords()));
    }

    @ApiOperation("获取我的待评审任务")
    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('expert','teacher','college_admin','school_admin')")
    public Result<List<MyReviewTaskDTO>> listMyReviewTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        List<MyReviewTaskDTO> tasks = expertAssignmentService.listMyReviewTasks(userId);
        return Result.success(tasks);
    }
}
