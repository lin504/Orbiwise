package com.lony.orbiwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * <p>仅使用 BCryptPasswordEncoder 进行密码加密，禁用 Spring Security 默认的安全机制。
 * JWT 鉴权由自定义的 JwtInterceptor 实现。</p>
 *
 * @author lin504
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 BCrypt 密码编码器
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     * <p>禁用 CSRF、Session、表单登录等默认安全机制，所有请求放行，由 JWT 拦截器负责鉴权</p>
     *
     * @param http HttpSecurity 构建器
     * @return SecurityFilterChain 过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

}
