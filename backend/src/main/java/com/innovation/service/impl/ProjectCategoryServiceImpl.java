package com.innovation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.ProjectCategory;
import com.innovation.mapper.ProjectCategoryMapper;
import com.innovation.service.ProjectCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ProjectCategoryServiceImpl extends ServiceImpl<ProjectCategoryMapper, ProjectCategory> implements ProjectCategoryService {
}
