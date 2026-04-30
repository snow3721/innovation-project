package com.innovation.controller;

import com.innovation.common.Constants;
import com.innovation.common.Result;
import com.innovation.dto.LoginDTO;
import com.innovation.dto.RegisterDTO;
import com.innovation.entity.User;
import com.innovation.security.TokenBlacklistEntry;
import com.innovation.security.TokenBlacklistRepository;
import com.innovation.service.UserService;
import com.innovation.util.JwtUtil;
import com.innovation.util.PasswordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
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

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

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
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole(), user.getCollegeId());
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
        // 注册只能为学生角色，防止越权注册管理员
        user.setRole("student");
        user.setCollegeId(dto.getCollegeId());
        user.setMajor(dto.getMajor());
        user.setStatus(1);
        userService.save(user);
        return Result.success();
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            String token = bearerToken.substring(Constants.TOKEN_PREFIX.length());
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    String jti = jwtUtil.getJti(token);
                    Instant expiresAt = jwtUtil.getExpiration(token).toInstant();
                    tokenBlacklistRepository.save(new TokenBlacklistEntry(jti, expiresAt, "logout"));
                }
            } catch (Exception ignored) {
                // Token 已过期或无效，无需加入黑名单
            }
        }
        return Result.success();
    }
}
