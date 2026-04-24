package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.dto.LoginDTO;
import com.innovation.dto.RegisterDTO;
import com.innovation.entity.User;
import com.innovation.service.UserService;
import com.innovation.util.JwtUtil;
import com.innovation.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            return Result.error(401, "密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("collegeId", user.getCollegeId());
        return Result.success(data);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        User existing = userService.findByUsername(dto.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setCollegeId(dto.getCollegeId());
        user.setMajor(dto.getMajor());
        user.setStatus(1);
        userService.save(user);
        return Result.success();
    }
}
