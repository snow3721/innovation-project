package com.innovation.security;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Token 黑名单文档（存储在 MongoDB 中）
 * 用于：用户登出、账号禁用、密码修改时使旧 Token 失效
 */
@Data
@Document(collection = "token_blacklist")
public class TokenBlacklistEntry {

    @Id
    private String id;

    /** 被拉黑的 JWT Token 的 jti（唯一标识） */
    @Indexed(unique = true)
    private String jti;

    /** Token 过期时间，黑名单记录在此时间后可自动清理 */
    @Indexed(expireAfterSeconds = 0)
    private Instant expiresAt;

    /** 拉黑原因：logout / disabled / password_change */
    private String reason;

    /** 拉黑时间 */
    private Instant createdAt;

    public TokenBlacklistEntry() {}

    public TokenBlacklistEntry(String jti, Instant expiresAt, String reason) {
        this.jti = jti;
        this.expiresAt = expiresAt;
        this.reason = reason;
        this.createdAt = Instant.now();
    }
}
