package com.lony.orbiwise.service;

import com.lony.orbiwise.entity.SensitiveWord;

import java.util.List;

/**
 * 敏感词服务接口
 * <p>提供敏感词的管理和文本检测功能，基于 Trie 树实现高效匹配</p>
 *
 * @author lin504
 */
public interface SensitiveWordService {

    /**
     * 初始化 Trie 树
     * <p>从数据库加载所有启用的敏感词构建 Trie 树，并存入 Redis 缓存</p>
     */
    void initTrie();

    /**
     * 检测文本是否包含敏感词
     *
     * @param text 待检测文本
     * @return 匹配到的第一个敏感词，无则返回 null
     */
    String checkText(String text);

    /**
     * 新增敏感词（操作后重新加载 Trie 树）
     *
     * @param sensitiveWord 敏感词实体
     */
    void addWord(SensitiveWord sensitiveWord);

    /**
     * 更新敏感词
     *
     * @param sensitiveWord 敏感词实体
     */
    void updateWord(SensitiveWord sensitiveWord);

    /**
     * 删除敏感词（操作后重新加载 Trie 树）
     *
     * @param id 敏感词ID
     */
    void deleteWord(Long id);

    /**
     * 查询所有敏感词列表
     *
     * @return 敏感词列表
     */
    List<SensitiveWord> listWords();

}
