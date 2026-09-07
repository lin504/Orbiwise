package com.lony.orbiwise.controller;

import com.lony.orbiwise.dto.StrategyDTO;
import com.lony.orbiwise.service.StrategyService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.StrategyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 旅游攻略控制器
 * <p>提供攻略的查询、发布、点赞、收藏接口</p>
 *
 * @author lin504
 */
@Tag(name = "攻略管理", description = "攻略发布、查询、点赞、收藏接口")
@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    /**
     * 攻略列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param scenicId 景点ID筛选
     * @param keyword  关键词搜索
     * @return 分页攻略列表
     */
    @Operation(summary = "攻略列表", description = "分页查询攻略，支持景点筛选和关键词搜索")
    @GetMapping("/list")
    public Result<PageVO<StrategyVO>> list(
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_NUM) Integer page,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) String keyword) {
        PageVO<StrategyVO> result = strategyService.listStrategy(page, size, scenicId, keyword);
        return Result.success(result);
    }

    /**
     * 攻略详情
     *
     * @param id 攻略ID
     * @return 攻略详情
     */
    @Operation(summary = "攻略详情", description = "获取攻略详细信息")
    @GetMapping("/{id}")
    public Result<StrategyVO> detail(@PathVariable Long id) {
        StrategyVO strategyVO = strategyService.getStrategyDetail(id);
        return Result.success(strategyVO);
    }

    /**
     * 发布攻略
     *
     * @param request     HTTP 请求
     * @param strategyDTO 攻略参数
     * @return 攻略ID
     */
    @Operation(summary = "发布攻略", description = "发布旅游攻略")
    @PostMapping
    public Result<Long> publish(HttpServletRequest request,
                                @Valid @RequestBody StrategyDTO strategyDTO) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        Long strategyId = strategyService.publishStrategy(userId, strategyDTO);
        return Result.success(strategyId);
    }

    /**
     * 点赞攻略
     *
     * @param request HTTP 请求
     * @param id      攻略ID
     * @return 操作结果
     */
    @Operation(summary = "点赞攻略", description = "为攻略点赞")
    @PutMapping("/{id}/like")
    public Result<Void> like(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        strategyService.likeStrategy(userId, id);
        return Result.success();
    }

    /**
     * 收藏攻略
     *
     * @param request HTTP 请求
     * @param id      攻略ID
     * @return 操作结果
     */
    @Operation(summary = "收藏攻略", description = "收藏旅游攻略")
    @PutMapping("/{id}/collect")
    public Result<Void> collect(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        strategyService.collectStrategy(userId, id);
        return Result.success();
    }

}
