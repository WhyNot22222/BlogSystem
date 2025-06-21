package com.yn.service.strategy;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 热门文章策略：获取热门文章作为后备策略（浏览量为王）
 */
@Component
public class PopularRelatedPostStrategy implements RelatedPostStrategy {

    @Resource
    private PostMapper postMapper;

    @Override
    public List<Post> execute(Post currentPost, Long excludePostId, int limit) {
        // 直接获取浏览量最高的几篇文章
        return postMapper.selectPopularPosts(excludePostId, limit);
    }
}