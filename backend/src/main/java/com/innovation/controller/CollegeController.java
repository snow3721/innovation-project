package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.entity.College;
import com.innovation.service.CollegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学院管理")
@RestController
@RequestMapping("/api/v1/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @ApiOperation("获取学院列表")
    @GetMapping
    public Result<List<College>> listColleges() {
        return Result.success(collegeService.list());
    }

    @ApiOperation("创建学院")
    @PostMapping
    public Result<Void> createCollege(@RequestBody College college) {
        collegeService.save(college);
        return Result.success();
    }

    @ApiOperation("更新学院")
    @PutMapping("/{id}")
    public Result<Void> updateCollege(@PathVariable Integer id, @RequestBody College college) {
        college.setCollegeId(id);
        collegeService.updateById(college);
        return Result.success();
    }

    @ApiOperation("删除学院")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCollege(@PathVariable Integer id) {
        collegeService.removeById(id);
        return Result.success();
    }
}
