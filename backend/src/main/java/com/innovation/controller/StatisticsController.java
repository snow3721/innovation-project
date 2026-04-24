package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "数据统计")
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("总览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    @ApiOperation("按年份统计")
    @GetMapping("/by-year")
    public Result<Map<String, Object>> getByYear(@RequestParam(required = false) Integer year) {
        return Result.success(statisticsService.getByYear(year));
    }

    @ApiOperation("按学院统计")
    @GetMapping("/by-college")
    public Result<Map<String, Object>> getByCollege(@RequestParam(required = false) Integer collegeId) {
        return Result.success(statisticsService.getByCollege(collegeId));
    }

    @ApiOperation("按类别统计")
    @GetMapping("/by-category")
    public Result<Map<String, Object>> getByCategory() {
        return Result.success(statisticsService.getByCategory());
    }
}
