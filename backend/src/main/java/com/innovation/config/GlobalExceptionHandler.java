package com.innovation.config;

import com.innovation.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 并发冲突：乐观锁版本号不匹配，或其他"先读后写"冲突
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("已被其他用户修改")) {
            log.warn("并发冲突: {}", msg);
            return Result.error(409, msg);
        }
        log.error("业务异常: {}", msg, e);
        return Result.error(400, msg != null ? msg : "操作失败");
    }

    /**
     * Spring Security 权限不足（@PreAuthorize 拒绝）
     * 注意：HTTP 状态码保持 200，通过业务 code 标识权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.error(403, "权限不足，请联系管理员");
    }

    /**
     * 其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }
}
