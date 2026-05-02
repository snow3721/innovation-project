package com.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String role = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("");
        List<MyReviewTaskDTO> tasks = expertAssignmentService.listMyReviewTasks(userId, role);
        return Result.success(tasks);
    }

    @ApiOperation("获取待分配项目列表")
    @GetMapping("/pending-projects")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<PageResult<Project>> listPendingProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Integer collegeId,
            @RequestParam(required = false) String projectName) {
        // 根据管理员角色和stage参数确定查询哪些状态
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if ("college".equals(stage)) {
            wrapper.eq(Project::getStatus, "wait_college_assign");
        } else if ("school".equals(stage)) {
            wrapper.eq(Project::getStatus, "wait_school_assign");
        } else {
            wrapper.in(Project::getStatus, "wait_college_assign", "wait_school_assign");
        }
        if (collegeId != null) {
            wrapper.eq(Project::getCollegeId, collegeId);
        }
        if (projectName != null && !projectName.isEmpty()) {
            wrapper.like(Project::getProjectName, projectName);
        }
        wrapper.orderByAsc(Project::getApplyTime);
        IPage<Project> projectPage = projectService.page(new Page<>(page, size), wrapper);
        // 填充关联名称
        projectPage.getRecords().forEach(p -> {
            Project detail = projectService.getProjectDetail(p.getProjectId());
            if (detail != null) {
                p.setLeaderName(detail.getLeaderName());
                p.setTeacherName(detail.getTeacherName());
                p.setCollegeName(detail.getCollegeName());
                p.setCatName(detail.getCatName());
                p.setStatusText(detail.getStatusText());
            }
        });
        return Result.success(new PageResult<>(projectPage.getTotal(), projectPage.getRecords()));
    }

    @ApiOperation("评审概览统计")
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        overview.put("collegeAssignCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_college_assign")));
        overview.put("collegeReviewCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_college_review")));
        overview.put("collegeAuditCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_college_audit")));
        overview.put("schoolAssignCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_school_assign")));
        overview.put("schoolReviewCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_school_review")));
        overview.put("schoolAuditCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "wait_school_audit")));
        overview.put("approvedCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "approved")));
        overview.put("rejectedCount", projectService.count(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "rejected")));
        overview.put("totalAssignments", expertAssignmentService.count());
        overview.put("totalScores", reviewScoreService.count());

        return Result.success(overview);
    }
}
