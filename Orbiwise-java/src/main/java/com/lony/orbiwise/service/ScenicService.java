package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.ScenicDTO;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.ScenicListVO;
import com.lony.orbiwise.vo.ScenicVO;

/**
 * 景点服务接口
 * <p>提供景点的查询、管理等功能</p>
 *
 * @author lin504
 */
public interface ScenicService {

    /**
     * 分页查询景点列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param category 分类筛选（可选）
     * @param keyword  关键词搜索（可选）
     * @return 分页景点列表
     */
    PageVO<ScenicListVO> listScenic(Integer page, Integer size, String category, String keyword);

    /**
     * 获取景点详情（含门票列表），增加浏览次数
     *
     * @param id 景点ID
     * @return 景点详情
     */
    ScenicVO getScenicDetail(Long id);

    /**
     * 新增景点
     *
     * @param scenicDTO 景点参数
     */
    void createScenic(ScenicDTO scenicDTO);

    /**
     * 编辑景点
     *
     * @param id        景点ID
     * @param scenicDTO 景点参数
     */
    void updateScenic(Long id, ScenicDTO scenicDTO);

    /**
     * 删除景点
     *
     * @param id 景点ID
     */
    void deleteScenic(Long id);

}
