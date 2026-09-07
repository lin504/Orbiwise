package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lony.orbiwise.dto.ScenicDTO;
import com.lony.orbiwise.entity.Scenic;
import com.lony.orbiwise.entity.Ticket;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.ScenicMapper;
import com.lony.orbiwise.mapper.TicketMapper;
import com.lony.orbiwise.service.ScenicService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.ScenicListVO;
import com.lony.orbiwise.vo.ScenicVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点服务实现类
 * <p>实现景点的查询、管理等功能</p>
 *
 * @author lin504
 */
@Service
public class ScenicServiceImpl implements ScenicService {

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private TicketMapper ticketMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageVO<ScenicListVO> listScenic(Integer page, Integer size, String category, String keyword) {
        // 构建分页对象
        Page<Scenic> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<Scenic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scenic::getStatus, Constants.SCENIC_STATUS_ON);

        // 分类筛选
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Scenic::getCategory, category);
        }

        // 关键词搜索（名称或描述模糊匹配）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Scenic::getName, keyword)
                    .or()
                    .like(Scenic::getDescription, keyword)
            );
        }

        queryWrapper.orderByDesc(Scenic::getCreateTime);

        // 执行分页查询
        Page<Scenic> resultPage = scenicMapper.selectPage(pageParam, queryWrapper);

        // 转换为列表 VO
        List<ScenicListVO> voList = resultPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, resultPage.getTotal(), page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScenicVO getScenicDetail(Long id) {
        Scenic scenic = scenicMapper.selectById(id);
        if (scenic == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }

        // 增加浏览次数
        scenicMapper.update(null, new LambdaUpdateWrapper<Scenic>()
                .eq(Scenic::getId, id)
                .set(Scenic::getViewCount, scenic.getViewCount() + 1));

        // 查询关联门票列表
        LambdaQueryWrapper<Ticket> ticketQuery = new LambdaQueryWrapper<>();
        ticketQuery.eq(Ticket::getScenicId, id)
                .eq(Ticket::getStatus, Constants.SCENIC_STATUS_ON);
        List<Ticket> tickets = ticketMapper.selectList(ticketQuery);

        // 转换为详情 VO
        ScenicVO vo = new ScenicVO();
        BeanUtils.copyProperties(scenic, vo);
        vo.setViewCount(scenic.getViewCount() + 1);

        // 转换门票列表
        List<ScenicVO.TicketVO> ticketVOs = tickets.stream().map(ticket -> {
            ScenicVO.TicketVO ticketVO = new ScenicVO.TicketVO();
            ticketVO.setId(ticket.getId());
            ticketVO.setTicketName(ticket.getTicketName());
            ticketVO.setPrice(ticket.getPrice());
            ticketVO.setOriginalPrice(ticket.getOriginalPrice());
            ticketVO.setTicketType(ticket.getTicketType());
            return ticketVO;
        }).collect(Collectors.toList());
        vo.setTickets(ticketVOs);

        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createScenic(ScenicDTO scenicDTO) {
        Scenic scenic = new Scenic();
        BeanUtils.copyProperties(scenicDTO, scenic);
        scenic.setViewCount(0);
        scenic.setCreateTime(LocalDateTime.now());
        scenic.setUpdateTime(LocalDateTime.now());
        scenicMapper.insert(scenic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateScenic(Long id, ScenicDTO scenicDTO) {
        Scenic scenic = scenicMapper.selectById(id);
        if (scenic == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }
        BeanUtils.copyProperties(scenicDTO, scenic);
        scenic.setUpdateTime(LocalDateTime.now());
        scenicMapper.updateById(scenic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteScenic(Long id) {
        Scenic scenic = scenicMapper.selectById(id);
        if (scenic == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }
        scenicMapper.deleteById(id);
    }

    /**
     * 将景点实体转换为列表 VO
     *
     * @param scenic 景点实体
     * @return 列表 VO
     */
    private ScenicListVO convertToListVO(Scenic scenic) {
        ScenicListVO vo = new ScenicListVO();
        vo.setId(scenic.getId());
        vo.setName(scenic.getName());
        vo.setCoverImage(scenic.getCoverImage());
        vo.setAddress(scenic.getAddress());
        vo.setCategory(scenic.getCategory());
        vo.setLevel(scenic.getLevel());
        vo.setAvgRating(scenic.getAvgRating());
        vo.setViewCount(scenic.getViewCount());
        return vo;
    }

}
