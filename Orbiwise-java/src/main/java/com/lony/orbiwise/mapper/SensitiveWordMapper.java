package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感词 Mapper 接口
 * <p>提供敏感词表的基础 CRUD 操作，支持查询所有启用的敏感词</p>
 *
 * @author lin504
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

}
