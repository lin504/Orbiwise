package com.lony.orbiwise.config;

import com.lony.orbiwise.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JWT 拦截器注册配置
 * <p>将 JwtInterceptor 注册到 /api/** 路径，排除 /api/auth/** 登录注册路径</p>
 *
 * @author lin504
 */
@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册 JWT 拦截器
     * <p>拦截所有 /api/** 请求，排除认证相关接口</p>
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/scenic/list",
                        "/api/scenic/{id}",
                        "/api/comment/scenic/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                );
    }

}
