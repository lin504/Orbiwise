package com.lony.orbiwise.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类
 * <p>提供 JWT token 的生成、解析、验证等功能，使用 HMAC-SHA256 签名算法</p>
 *
 * @author lin504
 */
@Component
public class JwtUtil {

    /** JWT 签名密钥，从配置文件读取 */
    @Value("${jwt.secret-key}")
    private String secretKey;

    /** JWT 过期时间（毫秒），从配置文件读取 */
    @Value("${jwt.expiration}")
    private Long expiration;

    /** JWT Claims 中的用户ID字段名 */
    private static final String CLAIM_USER_ID = "userId";

    /** JWT Claims 中的用户名字段名 */
    private static final String CLAIM_USERNAME = "username";

    /** JWT Claims 中的角色列表字段名 */
    private static final String CLAIM_ROLES = "roles";

    /**
     * 获取签名密钥
     *
     * @return HMAC-SHA256 签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    角色列表
     * @return JWT token 字符串
     */
    public String generateToken(Long userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLES, roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT Token，获取 Claims
     *
     * @param token JWT token 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT token 字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_USER_ID, Long.class);
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT token 字符串
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_USERNAME, String.class);
    }

    /**
     * 从 Token 中获取角色列表
     *
     * @param token JWT token 字符串
     * @return 角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = parseToken(token);
        return claims.get(CLAIM_ROLES, List.class);
    }

    /**
     * 判断 Token 是否已过期
     *
     * @param token JWT token 字符串
     * @return true-已过期，false-未过期
     */
    public boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // 解析失败视为已过期
            return true;
        }
    }

}
