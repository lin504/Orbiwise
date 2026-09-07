package com.lony.orbiwise.controller;

import com.lony.orbiwise.service.RecommendService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.RecommendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 * <p>提供基于协同过滤算法的个性化景点推荐接口</p>
 *
 * @author lin504
 */
@Tag(name = "推荐服务", description = "基于协同过滤的个性化推荐接口")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐
     *
     * @param request HTTP 请求
     * @param topN    推荐数量
     * @return 推荐结果列表
     */
    @Operation(summary = "个性化推荐", description = "基于协同过滤算法获取个性化景点推荐")
    @GetMapping("/list")
    public Result<List<RecommendVO>> recommend(
            HttpServletRequest request,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_RECOMMEND_TOP_N) Integer topN) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        List<RecommendVO> recommendations = recommendService.getRecommendations(userId, topN);
        return Result.success(recommendations);
    }

}
