package com.lony.orbiwise.interceptor;

import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * JWT 鉴权拦截器
 * <p>拦截所有需要认证的 API 请求，从 Authorization Header 中提取并验证 JWT Token，
 * 将用户信息存入 Request Attribute 供后续 Controller 和 AOP 使用</p>
 *
 * @author lin504
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求预处理：验证 JWT Token
     * <p>处理流程：
     * <ol>
     *   <li>从请求头获取 Authorization 字段</li>
     *   <li>检查是否以 "Bearer " 前缀开头</li>
     *   <li>提取 Token 并解析</li>
     *   <li>将 userId、username、roles 存入 request attribute</li>
     *   <li>Token 无效则返回 401</li>
     * </ol>
     * </p>
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return true-放行，false-拦截
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取 Authorization
        String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);

        // 检查 Authorization 是否存在和格式是否正确
        if (authHeader == null || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            log.warn("缺少有效的 Authorization 头: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\",\"data\":null}");
            return false;
        }

        // 提取 Token（去掉 "Bearer " 前缀）
        String token = authHeader.substring(Constants.TOKEN_PREFIX.length());

        try {
            // 检查 Token 是否过期
            if (jwtUtil.isExpired(token)) {
                log.warn("Token 已过期: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token已过期，请重新登录\",\"data\":null}");
                return false;
            }

            // 解析 Token 获取 Claims
            Claims claims = jwtUtil.parseToken(token);

            // 将用户信息存入 request attribute，供 Controller 和 AOP 使用
            Long userId = claims.get(Constants.REQUEST_ATTR_USER_ID, Long.class);
            String username = claims.get(Constants.REQUEST_ATTR_USERNAME, String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            request.setAttribute(Constants.REQUEST_ATTR_USER_ID, userId);
            request.setAttribute(Constants.REQUEST_ATTR_USERNAME, username);
            request.setAttribute(Constants.REQUEST_ATTR_ROLES, roles);

            return true;

        } catch (Exception e) {
            log.error("Token 解析失败: {}, error: {}", request.getRequestURI(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效，请重新登录\",\"data\":null}");
            return false;
        }
    }

}
