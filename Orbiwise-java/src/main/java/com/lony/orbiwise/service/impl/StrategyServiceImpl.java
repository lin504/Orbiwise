package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lony.orbiwise.dto.StrategyDTO;
import com.lony.orbiwise.entity.Scenic;
import com.lony.orbiwise.entity.Strategy;
import com.lony.orbiwise.entity.User;
import com.lony.orbiwise.entity.UserBehavior;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.ScenicMapper;
import com.lony.orbiwise.mapper.StrategyMapper;
import com.lony.orbiwise.mapper.UserBehaviorMapper;
import com.lony.orbiwise.mapper.UserMapper;
import com.lony.orbiwise.service.StrategyService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.StrategyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旅游攻略服务实现类
 * <p>实现攻略的查询、发布、点赞、收藏等功能</p>
 *
 * @author lin504
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageVO<StrategyVO> listStrategy(Integer page, Integer size, Long scenicId, String keyword) {
        Page<Strategy> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Strategy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Strategy::getStatus, Constants.STRATEGY_STATUS_PUBLISHED);

        // 按景点筛选
        if (scenicId != null) {
            queryWrapper.eq(Strategy::getScenicId, scenicId);
        }

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Strategy::getTitle, keyword)
                    .or()
                    .like(Strategy::getContent, keyword)
            );
        }

        queryWrapper.orderByDesc(Strategy::getCreateTime);

        Page<Strategy> resultPage = strategyMapper.selectPage(pageParam, queryWrapper);

        List<StrategyVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, resultPage.getTotal(), page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyVO getStrategyDetail(Long id) {
        Strategy strategy = strategyMapper.selectById(id);
        if (strategy == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "攻略不存在");
        }

        // 增加浏览次数
        strategyMapper.update(null, new LambdaUpdateWrapper<Strategy>()
                .eq(Strategy::getId, id)
                .set(Strategy::getViewCount, strategy.getViewCount() + 1));

        StrategyVO vo = convertToVO(strategy);
        vo.setViewCount(strategy.getViewCount() + 1);
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long publishStrategy(Long userId, StrategyDTO strategyDTO) {
        Strategy strategy = new Strategy();
        strategy.setUserId(userId);
        strategy.setScenicId(strategyDTO.getScenicId());
        strategy.setTitle(strategyDTO.getTitle());
        strategy.setContent(strategyDTO.getContent());
        strategy.setCoverImage(strategyDTO.getCoverImage());
        strategy.setTags(strategyDTO.getTags());
        strategy.setViewCount(0);
        strategy.setLikeCount(0);
        strategy.setCollectCount(0);
        strategy.setStatus(Constants.STRATEGY_STATUS_PUBLISHED);
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setUpdateTime(LocalDateTime.now());

        strategyMapper.insert(strategy);
        return strategy.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeStrategy(Long userId, Long strategyId) {
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "攻略不存在");
        }

        // 点赞数 +1
        strategyMapper.update(null, new LambdaUpdateWrapper<Strategy>()
                .eq(Strategy::getId, strategyId)
                .set(Strategy::getLikeCount, strategy.getLikeCount() + 1));

        // 记录用户点赞行为
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTargetId(strategyId);
        behavior.setTargetType("strategy");
        behavior.setBehaviorType("like");
        behavior.setBehaviorDesc("点赞攻略：" + strategyId);
        behavior.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collectStrategy(Long userId, Long strategyId) {
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "攻略不存在");
        }

        // 收藏数 +1
        strategyMapper.update(null, new LambdaUpdateWrapper<Strategy>()
                .eq(Strategy::getId, strategyId)
                .set(Strategy::getCollectCount, strategy.getCollectCount() + 1));

        // 记录用户收藏行为
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTargetId(strategy.getScenicId());
        behavior.setTargetType("scenic");
        behavior.setBehaviorType("collect");
        behavior.setBehaviorDesc("收藏攻略关联景点：" + strategy.getScenicId());
        behavior.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);
    }

    /**
     * 将攻略实体转换为 VO
     *
     * @param strategy 攻略实体
     * @return 攻略 VO
     */
    private StrategyVO convertToVO(Strategy strategy) {
        StrategyVO vo = new StrategyVO();
        vo.setId(strategy.getId());
        vo.setUserId(strategy.getUserId());
        vo.setScenicId(strategy.getScenicId());
        vo.setTitle(strategy.getTitle());
        vo.setContent(strategy.getContent());
        vo.setCoverImage(strategy.getCoverImage());
        vo.setTags(strategy.getTags());
        vo.setViewCount(strategy.getViewCount());
        vo.setLikeCount(strategy.getLikeCount());
        vo.setCollectCount(strategy.getCollectCount());
        vo.setStatus(strategy.getStatus());
        vo.setCreateTime(strategy.getCreateTime());

        // 查询作者信息
        User author = userMapper.selectById(strategy.getUserId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }

        // 查询景点名称
        Scenic scenic = scenicMapper.selectById(strategy.getScenicId());
        if (scenic != null) {
            vo.setScenicName(scenic.getName());
        }

        return vo;
    }

}
