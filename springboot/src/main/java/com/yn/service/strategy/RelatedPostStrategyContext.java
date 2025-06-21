package com.yn.service.strategy;

import com.yn.entity.Post;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 策略上下文：协调多种相关文章获取策略的执行
 *
 * 1. 按策略注入顺序执行（Spring会根据类名排序）
 * 2. 自动处理文章去重
 * 3. 确保最终结果数量满足需求
 */
@Component
public class RelatedPostStrategyContext {
    // 自动注入所有策略实现（按Bean名称排序）
    @Resource
    private List<RelatedPostStrategy> strategies;

    /**
     * 执行策略链获取相关文章
     *
     * @param currentPost 当前文章
     * @param excludePostId 需要排除的文章ID
     * @param limit 需要获取的文章数量
     * @return 去重后的相关文章列表
     */
    public List<Post> executeStrategies(Post currentPost, Long excludePostId, int limit) {
        Set<Long> addedIds = new HashSet<>();   // 记录当前已添加的文章ID
        addedIds.add(excludePostId); // 排除当前文章

        List<Post> result = new ArrayList<>();

        // 按策略顺序执行，直到达到需要的数量
        for (RelatedPostStrategy strategy : strategies) {
            if (result.size() >= limit) break;

            // 计算当前策略需要获取的数量
            int remaining = limit - result.size();
            List<Post> posts = strategy.execute(currentPost, excludePostId, remaining);

            // 过滤已添加的文章并加入结果集
            for (Post post : posts) {
                if (!addedIds.contains(post.getId())) {
                    result.add(post);
                    addedIds.add(post.getId());
                }
                // 达到数量要求时提前终止
                if (result.size() >= limit) break;
            }
        }

        return result;
    }
}