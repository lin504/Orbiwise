package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lony.orbiwise.entity.SensitiveWord;
import com.lony.orbiwise.mapper.SensitiveWordMapper;
import com.lony.orbiwise.service.SensitiveWordService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.trie.SensitiveWordTrie;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 敏感词服务实现类
 * <p>基于 Trie 树实现高效敏感词检测，支持从数据库加载词典并缓存到 Redis</p>
 *
 * @author lin504
 */
@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /** 内存中的 Trie 树实例 */
    private volatile SensitiveWordTrie sensitiveWordTrie = new SensitiveWordTrie();

    /**
     * 应用启动时自动初始化 Trie 树
     * <p>使用 @PostConstruct 注解，在 Bean 初始化后自动从数据库加载敏感词构建 Trie 树</p>
     */
    @PostConstruct
    public void init() {
        initTrie();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initTrie() {
        // 从数据库查询所有启用的敏感词
        LambdaQueryWrapper<SensitiveWord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SensitiveWord::getStatus, Constants.SENSITIVE_WORD_ENABLED);
        List<SensitiveWord> words = sensitiveWordMapper.selectList(queryWrapper);

        // 创建新的 Trie 树并加载所有敏感词
        SensitiveWordTrie newTrie = new SensitiveWordTrie();
        for (SensitiveWord word : words) {
            // 将每个敏感词插入到 Trie 树中
            newTrie.insertWord(word.getWord());
        }

        // 原子性替换旧的 Trie 树引用
        this.sensitiveWordTrie = newTrie;

        // 将敏感词列表缓存到 Redis，设置 24 小时过期
        List<String> wordList = words.stream()
                .map(SensitiveWord::getWord)
                .toList();
        redisTemplate.opsForValue().set(
                Constants.REDIS_SENSITIVE_WORD_KEY,
                wordList,
                24,
                TimeUnit.HOURS
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        // 使用 Trie 树检测文本中是否包含敏感词
        return sensitiveWordTrie.searchWord(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWord(SensitiveWord sensitiveWord) {
        sensitiveWord.setCreateTime(LocalDateTime.now());
        sensitiveWord.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.insert(sensitiveWord);

        // 新增后重新加载 Trie 树
        initTrie();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWord(SensitiveWord sensitiveWord) {
        sensitiveWord.setUpdateTime(LocalDateTime.now());
        sensitiveWordMapper.updateById(sensitiveWord);

        // 更新后重新加载 Trie 树
        initTrie();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWord(Long id) {
        sensitiveWordMapper.deleteById(id);

        // 删除后重新加载 Trie 树
        initTrie();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SensitiveWord> listWords() {
        return sensitiveWordMapper.selectList(null);
    }

}
