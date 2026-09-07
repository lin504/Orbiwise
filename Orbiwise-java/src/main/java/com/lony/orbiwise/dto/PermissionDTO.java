package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 权限分配请求DTO
 * <p>接收角色分配和权限分配时的参数</p>
 *
 * @author lin504
 */
@Data
public class PermissionDTO {

    /** 用户ID（分配角色时使用） */
    private Long userId;

    /** 角色ID（分配角色或权限时使用） */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 权限ID列表（分配权限时使用） */
    private List<Long> permIds;

}
