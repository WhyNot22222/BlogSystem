package com.yn.service.strategy;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 分类相关策略：获取相同分类的文章
 */
@Component
public class CategoryRelatedPostStrategy implements RelatedPostStrategy {

    @Resource
    private PostMapper postMapper;

    @Override
    public List<Post> execute(Post currentPost, Long excludePostId, int limit) {
        // 仅当当前文章有分类时执行
        if (currentPost.getCategoryId() == null) {
            return Collections.emptyList();
        }
        return postMapper.selectPostsByCategory(
                currentPost.getCategoryId(), excludePostId, limit);
    }
}