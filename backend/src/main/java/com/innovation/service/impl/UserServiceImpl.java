package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.User;
import com.innovation.mapper.UserMapper;
import com.innovation.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public IPage<User> listUsers(int page, int size, String role, Integer collegeId, String realName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (collegeId != null) {
            wrapper.eq(User::getCollegeId, collegeId);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<User> listTeachers(Integer collegeId, String realName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "teacher");
        wrapper.eq(User::getStatus, 1);
        if (collegeId != null) {
            wrapper.eq(User::getCollegeId, collegeId);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        wrapper.select(User::getUserId, User::getRealName, User::getCollegeId, User::getMajor, User::getPhone, User::getEmail);
        wrapper.orderByAsc(User::getRealName);
        return list(wrapper);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getRealName, keyword).or().like(User::getUsername, keyword));
        }
        wrapper.select(User::getUserId, User::getRealName, User::getUsername, User::getRole, User::getCollegeId);
        wrapper.orderByAsc(User::getRealName);
        wrapper.last("LIMIT 50");
        return list(wrapper);
    }
}
