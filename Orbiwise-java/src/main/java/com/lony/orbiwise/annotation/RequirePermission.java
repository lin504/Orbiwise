package com.lony.orbiwise.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * <p>标注在 Controller 方法上，用于声明该接口需要的权限编码。
 * PermissionAspect 切面会拦截带有此注解的方法，校验当前用户是否拥有所需权限。</p>
 *
 * <p>使用示例：
 * <pre>
 * {@code @RequirePermission({"scenic:manage"})}
 * public Result<Void> createScenic(...) { ... }
 * </pre>
 * </p>
 *
 * @author lin504
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 所需的权限编码数组
     * <p>用户必须拥有数组中所有权限才能访问该接口</p>
     *
     * @return 权限编码数组
     */
    String[] value();

}
