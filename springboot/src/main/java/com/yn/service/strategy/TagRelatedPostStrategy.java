package com.yn.service.strategy;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 标签相关策略：获取相同标签的文章
 */
@Component
public class TagRelatedPostStrategy implements RelatedPostStrategy {

    @Resource
    private PostMapper postMapper;

    @Override
    public List<Post> execute(Post currentPost, Long excludePostId, int limit) {
        // 获取当前文章的所有标签ID
        List<Long> tagIds = postMapper.selectTagIdsByPostId(excludePostId);
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        return postMapper.selectPostsByTags(tagIds, excludePostId, limit);
    }
}