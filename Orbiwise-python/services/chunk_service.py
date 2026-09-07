"""
文本分块服务模块
实现旅游攻略文本的智能分块策略
按段落优先分割，段落过长时按固定大小切分，相邻块保留重叠区域以保持语义连贯性
"""

import logging

logger = logging.getLogger(__name__)


class ChunkService:
    """
    文本分块服务
    将长文本切分为适合向量化和检索的片段
    """

    def chunk_text(
        self,
        text: str,
        chunk_size: int = 500,
        overlap: int = 50,
    ) -> list[str]:
        """
        将文本按段落优先策略进行分块

        分块策略：
        1. 先按段落（换行符）分割文本
        2. 将短段落合并到 chunk_size 大小
        3. 超长段落按 chunk_size 切分，保留 overlap 重叠

        Args:
            text: 待分块的原始文本
            chunk_size: 每个块的目标字符数，默认 500
            overlap: 相邻块之间的重叠字符数，默认 50

        Returns:
            list[str]: 分块后的文本列表
        """
        # 去除文本首尾空白字符
        text = text.strip()
        if not text:
            return []

        # 第一步：按段落分割文本
        # 使用双换行符分割，保留段落结构
        paragraphs = text.split("\n\n")
        # 过滤掉空段落（连续换行产生的空字符串）
        paragraphs = [p.strip() for p in paragraphs if p.strip()]

        chunks: list[str] = []
        # 第二步：将段落合并或拆分到目标大小
        current_chunk = ""

        for paragraph in paragraphs:
            # 检查当前段落是否超过 chunk_size
            if len(paragraph) > chunk_size:
                # 段落过长，先将之前积累的文本保存为一个块
                if current_chunk:
                    chunks.append(current_chunk.strip())
                    current_chunk = ""
                # 对超长段落进行滑动窗口切分
                # 使用 chunk_size 作为窗口大小，overlap 作为步长偏移
                sub_chunks = self._split_long_text(paragraph, chunk_size, overlap)
                chunks.extend(sub_chunks)
            else:
                # 段落长度在 chunk_size 以内，尝试合并到当前块
                # 合并后不超过 chunk_size 则直接拼接
                if len(current_chunk) + len(paragraph) + 2 <= chunk_size:
                    # 用双换行符连接段落，保留段落分隔
                    current_chunk = (
                        current_chunk + "\n\n" + paragraph
                        if current_chunk
                        else paragraph
                    )
                else:
                    # 合并后会超出 chunk_size，保存当前块并开始新块
                    if current_chunk:
                        chunks.append(current_chunk.strip())
                    current_chunk = paragraph

        # 将最后一个未保存的块加入结果列表
        if current_chunk:
            chunks.append(current_chunk.strip())

        logger.info(f"文本分块完成，共生成 {len(chunks)} 个块")
        return chunks

    def _split_long_text(
        self,
        text: str,
        chunk_size: int,
        overlap: int,
    ) -> list[str]:
        """
        对超长文本进行滑动窗口切分

        使用固定窗口大小和重叠区域，确保不丢失任何内容
        重叠区域保证相邻块的语义连贯性

        Args:
            text: 需要切分的长文本
            chunk_size: 每个块的字符数
            overlap: 相邻块之间的重叠字符数

        Returns:
            list[str]: 切分后的文本块列表
        """
        chunks: list[str] = []
        # 计算每次滑动的步长 = 窗口大小 - 重叠大小
        step = chunk_size - overlap

        # 确保步长大于 0，避免死循环
        if step <= 0:
            step = chunk_size

        start = 0
        while start < len(text):
            # 从 start 位置截取 chunk_size 长度的文本
            end = start + chunk_size
            chunk = text[start:end]
            chunks.append(chunk.strip())

            # 滑动窗口，移动到下一个位置
            start += step

        return chunks
