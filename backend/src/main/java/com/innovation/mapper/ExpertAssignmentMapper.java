package com.innovation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.innovation.entity.ExpertAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExpertAssignmentMapper extends BaseMapper<ExpertAssignment> {

    IPage<ExpertAssignment> selectAssignmentPageWithDetail(Page<ExpertAssignment> page,
                                                           @Param("projectId") Integer projectId,
                                                           @Param("stage") String stage);
}
