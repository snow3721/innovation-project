package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.entity.User;
import com.innovation.service.UserService;
import com.innovation.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取当前用户信息")
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("collegeId", user.getCollegeId());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("major", user.getMajor());
        return Result.success(data);
    }

    @ApiOperation("获取用户列表")
    @GetMapping
    public Result<PageResult<User>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer collegeId,
            @RequestParam(required = false) String realName) {
        IPage<User> userPage = userService.listUsers(page, size, role, collegeId, realName);
        return Result.success(new PageResult<>(userPage.getTotal(), userPage.getRecords()));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation("创建用户")
    @PostMapping
    public Result<Void> createUser(@RequestBody User user) {
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        userService.save(user);
        return Result.success();
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setUserId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }
        userService.updateById(user);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        userService.removeById(id);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        User user = userService.getById(userId);
        if (!PasswordUtil.matches(body.get("oldPassword"), user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(PasswordUtil.encrypt(body.get("newPassword")));
        userService.updateById(user);
        return Result.success();
    }
}
