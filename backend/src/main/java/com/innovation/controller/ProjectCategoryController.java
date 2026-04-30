package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.entity.ProjectCategory;
import com.innovation.service.ProjectCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目类别管理")
@RestController
@RequestMapping("/api/v1/categories")
public class ProjectCategoryController {

    @Autowired
    private ProjectCategoryService categoryService;

    @ApiOperation("获取类别列表")
    @GetMapping
    public Result<List<ProjectCategory>> listCategories() {
        return Result.success(categoryService.list());
    }

    @ApiOperation("创建类别")
    @PostMapping
    @PreAuthorize("hasRole('school_admin')")
    public Result<Void> createCategory(@RequestBody ProjectCategory category) {
        categoryService.save(category);
        return Result.success();
    }

    @ApiOperation("更新类别")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('school_admin')")
    public Result<Void> updateCategory(@PathVariable Integer id, @RequestBody ProjectCategory category) {
        category.setCatId(id);
        categoryService.updateById(category);
        return Result.success();
    }

    @ApiOperation("删除类别")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('school_admin')")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
