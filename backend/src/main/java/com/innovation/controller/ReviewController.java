package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.ReviewScoreDTO;
import com.innovation.entity.ExpertAssignment;
import com.innovation.entity.ProjectReviewScore;
import com.innovation.service.ExpertAssignmentService;
import com.innovation.service.ProjectReviewScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "评审管理")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ProjectReviewScoreService reviewScoreService;

    @Autowired
    private ExpertAssignmentService expertAssignmentService;

    @ApiOperation("专家提交评分")
    @PostMapping("/scores")
    public Result<ProjectReviewScore> submitScore(@Validated @RequestBody ReviewScoreDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(reviewScoreService.submitScore(dto, userId));
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
    public Result<Void> assignExpert(@RequestBody ExpertAssignment assignment) {
        expertAssignmentService.assignExpert(assignment.getProjectId(), assignment.getExpertId(), assignment.getStage());
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
}
