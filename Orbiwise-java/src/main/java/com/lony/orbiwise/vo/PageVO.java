package com.lony.orbiwise.vo;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应VO
 * <p>封装分页查询结果，包含数据列表和分页信息</p>
 *
 * @param <T> 数据类型
 * @author lin504
 */
@Data
public class PageVO<T> {

    /** 数据列表 */
    private List<T> list;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Integer page;

    /** 每页大小 */
    private Integer size;

    /**
     * 构建分页响应对象
     *
     * @param list  数据列表
     * @param total 总记录数
     * @param page  当前页码
     * @param size  每页大小
     * @param <T>   数据类型
     * @return 分页响应对象
     */
    public static <T> PageVO<T> of(List<T> list, Long total, Integer page, Integer size) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setList(list);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

}
