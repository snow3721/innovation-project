package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @ApiOperation("按类别统计")
    @GetMapping("/by-category")
    public Result<Map<String, Object>> getByCategory() {
        return Result.success(statisticsService.getByCategory());
    }

    @ApiOperation("按年份统计")
    @GetMapping("/by-year")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Map<String, Object>> getByYear(@RequestParam(required = false) Integer year) {
        return Result.success(statisticsService.getByYear(year));
    }

    @ApiOperation("按学院统计")
    @GetMapping("/by-college")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Map<String, Object>> getByCollege(@RequestParam(required = false) Integer collegeId) {
        return Result.success(statisticsService.getByCollege(collegeId));
    }

    @ApiOperation("导出统计数据Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=statistics_export.xlsx");

        Map<String, Object> overview = statisticsService.getOverview();
        Map<String, Object> byCategory = statisticsService.getByCategory();

        try (Workbook workbook = new XSSFWorkbook()) {
            // 概览Sheet
            Sheet overviewSheet = workbook.createSheet("数据概览");
            Row headerRow = overviewSheet.createRow(0);
            headerRow.createCell(0).setCellValue("指标");
            headerRow.createCell(1).setCellValue("数值");

            String[] overviewLabels = {"项目总数", "已立项项目数", "运行中项目数", "成果总数", "立项率(%)"};
            String[] overviewKeys = {"totalProjects", "approvedProjects", "runningProjects", "totalAchievements", "approvalRate"};
            for (int i = 0; i < overviewLabels.length; i++) {
                Row row = overviewSheet.createRow(i + 1);
                row.createCell(0).setCellValue(overviewLabels[i]);
                Object val = overview.get(overviewKeys[i]);
                row.createCell(1).setCellValue(val != null ? val.toString() : "0");
            }

            // 按类别Sheet
            Sheet categorySheet = workbook.createSheet("成果分类统计");
            Row catHeader = categorySheet.createRow(0);
            catHeader.createCell(0).setCellValue("成果类别");
            catHeader.createCell(1).setCellValue("数量");

            String[] catLabels = {"专利", "论文", "软件著作权", "竞赛获奖", "创业项目"};
            String[] catKeys = {"patent", "paper", "software", "competition", "business"};
            for (int i = 0; i < catLabels.length; i++) {
                Row row = categorySheet.createRow(i + 1);
                row.createCell(0).setCellValue(catLabels[i]);
                Object val = byCategory.get(catKeys[i]);
                row.createCell(1).setCellValue(val != null ? val.toString() : "0");
            }

            // 自动调整列宽
            overviewSheet.autoSizeColumn(0);
            overviewSheet.autoSizeColumn(1);
            categorySheet.autoSizeColumn(0);
            categorySheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }
}
