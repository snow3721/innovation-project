package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.entity.Expert;
import com.innovation.service.ExpertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "专家管理")
@RestController
@RequestMapping("/api/v1/experts")
public class ExpertController {

    @Autowired
    private ExpertService expertService;

    @ApiOperation("获取专家列表")
    @GetMapping
    public Result<PageResult<Expert>> listExperts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String researchField) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Expert> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(Expert::getRealName, realName);
        }
        if (researchField != null && !researchField.isEmpty()) {
            wrapper.like(Expert::getResearchField, researchField);
        }
        IPage<Expert> expertPage = expertService.page(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(expertPage.getTotal(), expertPage.getRecords()));
    }

    @ApiOperation("创建专家")
    @PostMapping
    public Result<Void> createExpert(@RequestBody Expert expert) {
        expertService.save(expert);
        return Result.success();
    }

    @ApiOperation("更新专家")
    @PutMapping("/{id}")
    public Result<Void> updateExpert(@PathVariable Integer id, @RequestBody Expert expert) {
        expert.setExpertId(id);
        expertService.updateById(expert);
        return Result.success();
    }

    @ApiOperation("删除专家")
    @DeleteMapping("/{id}")
    public Result<Void> deleteExpert(@PathVariable Integer id) {
        expertService.removeById(id);
        return Result.success();
    }
}
