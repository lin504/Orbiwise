package com.lony.orbiwise.util.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词 Trie 树过滤器
 * <p>基于 Trie（字典树）数据结构实现高效敏感词检测。
 * Trie 树是一种有序树，用于保存关联数组，其中键通常是字符串。
 * 从根节点到某一节点的路径上经过的字符连接起来，即为该节点对应的字符串。</p>
 *
 * <p>算法复杂度：
 * <ul>
 *   <li>插入：O(m)，m为词长</li>
 *   <li>查找：O(n)，n为文本长度</li>
 * </ul>
 * </p>
 *
 * @author lin504
 */
public class SensitiveWordTrie {

    /** Trie 树根节点，不存储字符，作为所有词的起始 */
    private final TrieNode root = new TrieNode();

    /**
     * Trie 树节点内部类
     * <p>每个节点包含子节点映射和结束标记</p>
     */
    public static class TrieNode {
        /** 子节点映射：字符 -> 子节点，使用 HashMap 支持动态添加 */
        private final Map<Character, TrieNode> children = new HashMap<>();

        /** 是否为某个敏感词的结尾节点 */
        private boolean isEnd = false;

        /**
         * 获取子节点映射
         *
         * @return 子节点 Map
         */
        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        /**
         * 设置是否为结束节点
         *
         * @param isEnd 是否为敏感词结尾
         */
        public void setIsEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }

        /**
         * 判断是否为结束节点
         *
         * @return true 表示该节点是某个敏感词的结尾
         */
        public boolean isEnd() {
            return isEnd;
        }
    }

    /**
     * 向 Trie 树中插入一个敏感词
     * <p>从根节点开始，逐字符构建路径。如果字符对应的子节点不存在则创建新节点。
     * 最后一个字符对应的节点标记为结束节点。</p>
     *
     * @param word 要插入的敏感词
     */
    public void insertWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        // 从根节点开始遍历
        TrieNode currentNode = root;

        // 逐个字符插入到 Trie 树中
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            // 如果当前字符对应的子节点不存在，创建新节点
            if (!currentNode.getChildren().containsKey(c)) {
                currentNode.getChildren().put(c, new TrieNode());
            }

            // 移动到下一个子节点
            currentNode = currentNode.getChildren().get(c);
        }

        // 标记词的结尾，表示从根到此节点的路径构成一个完整敏感词
        currentNode.setIsEnd(true);
    }

    /**
     * 检测文本中是否包含敏感词
     * <p>使用双指针法遍历文本：
     * <ol>
     *   <li>外层指针 i 从文本开头逐位移动</li>
     *   <li>内层指针 j 从 i 位置开始，沿 Trie 树向下匹配</li>
     *   <li>如果匹配到结束节点，说明找到敏感词，立即返回</li>
     *   <li>如果某字符无法继续匹配，则外层指针前移一位重新开始</li>
     * </ol>
     * </p>
     *
     * @param text 待检测的文本
     * @return 第一个匹配到的敏感词，如果没有则返回 null
     */
    public String searchWord(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 外层循环：遍历文本中的每个字符作为起始位置
        for (int i = 0; i < text.length(); i++) {
            // 从根节点开始匹配
            TrieNode currentNode = root;

            // 内层循环：从当前位置开始，沿 Trie 树向下匹配
            for (int j = i; j < text.length(); j++) {
                char c = text.charAt(j);

                // 如果当前字符在 Trie 树中没有对应子节点，说明以 i 开头无法构成敏感词
                if (!currentNode.getChildren().containsKey(c)) {
                    break;
                }

                // 移动到匹配的子节点
                currentNode = currentNode.getChildren().get(c);

                // 如果到达某个敏感词的结尾，返回匹配的敏感词
                if (currentNode.isEnd()) {
                    return text.substring(i, j + 1);
                }
            }
        }

        // 遍历完整个文本都没有找到敏感词
        return null;
    }

    /**
     * 清空 Trie 树
     * <p>重置根节点，清除所有已插入的敏感词</p>
     */
    public void clear() {
        root.getChildren().clear();
    }

}
