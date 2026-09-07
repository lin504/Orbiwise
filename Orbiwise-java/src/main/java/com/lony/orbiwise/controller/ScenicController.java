package com.lony.orbiwise.controller;

import com.lony.orbiwise.annotation.RequirePermission;
import com.lony.orbiwise.dto.ScenicDTO;
import com.lony.orbiwise.service.ScenicService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.ScenicListVO;
import com.lony.orbiwise.vo.ScenicVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 景点控制器
 * <p>提供景点的查询和管理接口</p>
 *
 * @author lin504
 */
@Tag(name = "景点管理", description = "景点增删改查接口")
@RestController
@RequestMapping("/api/scenic")
public class ScenicController {

    @Autowired
    private ScenicService scenicService;

    /**
     * 景点列表（分页）
     *
     * @param page     页码
     * @param size     每页大小
     * @param category 分类筛选
     * @param keyword  关键词搜索
     * @return 分页景点列表
     */
    @Operation(summary = "景点列表", description = "分页查询景点，支持分类筛选和关键词搜索")
    @GetMapping("/list")
    public Result<PageVO<ScenicListVO>> list(
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_NUM) Integer page,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        PageVO<ScenicListVO> result = scenicService.listScenic(page, size, category, keyword);
        return Result.success(result);
    }

    /**
     * 景点详情
     *
     * @param id 景点ID
     * @return 景点详情（含门票列表）
     */
    @Operation(summary = "景点详情", description = "获取景点详细信息，包含门票列表")
    @GetMapping("/{id}")
    public Result<ScenicVO> detail(@PathVariable Long id) {
        ScenicVO scenicVO = scenicService.getScenicDetail(id);
        return Result.success(scenicVO);
    }

    /**
     * 新增景点（需权限）
     *
     * @param scenicDTO 景点参数
     * @return 操作结果
     */
    @Operation(summary = "新增景点", description = "管理端新增景点，需要scenic:manage权限")
    @RequirePermission({"scenic:manage"})
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ScenicDTO scenicDTO) {
        scenicService.createScenic(scenicDTO);
        return Result.success();
    }

    /**
     * 编辑景点（需权限）
     *
     * @param id        景点ID
     * @param scenicDTO 景点参数
     * @return 操作结果
     */
    @Operation(summary = "编辑景点", description = "管理端编辑景点信息")
    @RequirePermission({"scenic:manage"})
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ScenicDTO scenicDTO) {
        scenicService.updateScenic(id, scenicDTO);
        return Result.success();
    }

    /**
     * 删除景点（需权限）
     *
     * @param id 景点ID
     * @return 操作结果
     */
    @Operation(summary = "删除景点", description = "管理端删除景点")
    @RequirePermission({"scenic:manage"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scenicService.deleteScenic(id);
        return Result.success();
    }

}
