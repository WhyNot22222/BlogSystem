package com.yn.service.strategy;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 作者相关策略：获取相同作者的文章
 */
@Component
public class AuthorRelatedPostStrategy implements RelatedPostStrategy {

    @Resource
    private PostMapper postMapper;

    @Override
    public List<Post> execute(Post currentPost, Long excludePostId, int limit) {
        if (currentPost.getUserId() == null) {
            return Collections.emptyList();
        }
        return postMapper.selectPostsByAuthor(
                currentPost.getUserId(), excludePostId, limit);
    }
}