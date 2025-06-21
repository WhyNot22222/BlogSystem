package com.yn.service.strategy;

import com.yn.entity.Post;

import java.util.List;

/**
 * 相关文章获取策略接口
 * 定义统一的策略执行方法，每种策略实现不同的相关文章获取逻辑
 */
public interface RelatedPostStrategy {
    /**
     * 执行策略获取相关文章
     *
     * @param currentPost 当前文章对象
     * @param excludePostId 需要排除的文章ID（当前文章）
     * @param limit 最大获取数量
     * @return 相关文章列表
     */
    List<Post> execute(Post currentPost, Long excludePostId, int limit);
}