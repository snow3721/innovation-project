package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    IPage<User> listUsers(int page, int size, String role, Integer collegeId, String realName);

    List<User> listTeachers(Integer collegeId, String realName);

    List<User> searchUsers(String keyword);
}
