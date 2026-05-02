package com.innovation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.innovation.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    IPage<Project> selectProjectPage(Page<Project> page,
                                      @Param("status") String status,
                                      @Param("collegeId") Integer collegeId,
                                      @Param("applyYear") Integer applyYear,
                                      @Param("projectName") String projectName,
                                      @Param("leaderId") Integer leaderId,
                                      @Param("teacherId") Integer teacherId,
                                      @Param("memberUserId") Integer memberUserId);
}
