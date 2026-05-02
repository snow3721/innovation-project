package com.innovation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.innovation.entity.ProjectReviewScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectReviewScoreMapper extends BaseMapper<ProjectReviewScore> {

    IPage<ProjectReviewScore> selectScorePageWithDetail(Page<ProjectReviewScore> page,
                                                        @Param("projectId") Integer projectId,
                                                        @Param("stage") String stage);
}
