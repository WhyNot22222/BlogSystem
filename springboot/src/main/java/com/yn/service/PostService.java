package com.yn.service;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import com.yn.service.strategy.RelatedPostStrategyContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    @Resource
    private PostMapper postMapper;

    // 使用策略上下文处理相关文章推荐逻辑
    @Resource
    private RelatedPostStrategyContext strategyContext;

    public Post createPost(Post post) {
        postMapper.insertPost(post);
        return post;
    }

    public Post updatePost(Post post) {
        int updatedRows = postMapper.updatePost(post);
        return updatedRows > 0 ? post : null;
    }

    public Post getPostById(Long id) {
        return postMapper.selectPostById(id);
    }

    public List<Post> getPublishedPosts() {
        return postMapper.selectPublishedPosts();
    }

    public List<Post> getPostsByFollowedUsers(Long followerId) {
        return postMapper.selectPostsByFollowerId(followerId);
    }

    public List<Post> getPostsByUserIds(List<Long> userIds) {
        return postMapper.selectPostsByUserIds(userIds);
    }

    public List<Post> getPostsByIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }
        return postMapper.selectPostsByIds(postIds);
    }

    /**
     * 获取相关文章（使用策略模式）
     *
     * @param postId 当前文章ID
     * @param limit 需要获取的相关文章数量
     * @return 推荐文章列表（已去重）
     */
    public List<Post> getRelatedPosts(Long postId, int limit) {
        Post currentPost = getPostById(postId);
        if (currentPost == null) {
            return Collections.emptyList();
        }
        // 通过策略上下文执行多种获取策略
        return strategyContext.executeStrategies(currentPost, postId, limit);
    }

    public void incrementViews(Long postId) {
        postMapper.incrementViews(postId);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postMapper.selectPostsByUserId(userId);
    }
}