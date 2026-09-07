package com.lony.orbiwise.aspect;

import com.lony.orbiwise.annotation.RequirePermission;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.service.PermissionService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * 权限校验 AOP 切面
 * <p>拦截所有标注了 @RequirePermission 注解的方法，
 * 从 Request 中获取当前用户信息，查询其权限列表，
 * 校验是否拥有所需权限。无权限则抛出 403 异常。</p>
 *
 * @author lin504
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    /**
     * 前置通知：在方法执行前进行权限校验
     * <p>校验流程：
     * <ol>
     *   <li>从注解中获取所需的权限编码数组</li>
     *   <li>从 Request Attribute 中获取当前用户ID</li>
     *   <li>查询用户拥有的所有权限编码</li>
     *   <li>校验用户权限是否包含所有所需权限</li>
     *   <li>缺少任一权限则抛出 FORBIDDEN 异常</li>
     * </ol>
     * </p>
     *
     * @param joinPoint        连接点
     * @param requirePermission 权限注解
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 获取注解中声明的所需权限编码
        String[] requiredPerms = requirePermission.value();
        if (requiredPerms.length == 0) {
            return;
        }

        // 从当前请求上下文中获取用户ID
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        HttpServletRequest request = attributes.getRequest();
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 查询用户拥有的所有权限编码
        List<String> userPerms = permissionService.getUserPermissions(userId);

        // 校验用户是否拥有所需的所有权限
        List<String> requiredList = Arrays.asList(requiredPerms);
        for (String perm : requiredList) {
            if (!userPerms.contains(perm)) {
                log.warn("用户 {} 缺少权限: {}, 访问方法: {}",
                        userId, perm, joinPoint.getSignature().toShortString());
                throw new BusinessException(ResultCode.FORBIDDEN,
                        "权限不足，需要权限: " + perm);
            }
        }
    }

}
