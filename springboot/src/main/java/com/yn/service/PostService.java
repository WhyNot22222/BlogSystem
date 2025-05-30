package com.yn.service;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    @Resource
    private PostMapper postMapper;

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

    public List<Post> getRelatedPosts(Long postId, int limit) {
        // 1. 获取当前帖子信息
        Post currentPost = getPostById(postId);
        if (currentPost == null) {
            return null;
        }

        // 2. 获取相关帖子
        List<Post> relatedPosts = new ArrayList<>();

        // 策略1：相同分类的帖子
        if (currentPost.getCategoryId() != null) {
            List<Post> sameCategory = postMapper.selectPostsByCategory(
                    currentPost.getCategoryId(),
                    postId,
                    limit
            );
            relatedPosts.addAll(sameCategory);
        }

        // 策略2：相同标签的帖子（如果策略1不够）
        if (relatedPosts.size() < limit) {
            List<Long> tagIds = postMapper.selectTagIdsByPostId(postId);
            if (!tagIds.isEmpty()) {
                List<Post> sameTags = postMapper.selectPostsByTags(
                        tagIds,
                        postId,
                        limit - relatedPosts.size()
                );
                relatedPosts.addAll(sameTags);
            }
        }

        // 策略3：相同作者的帖子（如果还不够）
        if (relatedPosts.size() < limit && currentPost.getUserId() != null) {
            List<Post> sameAuthor = postMapper.selectPostsByAuthor(
                    currentPost.getUserId(),
                    postId,
                    limit - relatedPosts.size()
            );
            relatedPosts.addAll(sameAuthor);
        }

        // 策略4：热门文章（作为后备）
        if (relatedPosts.size() < limit) {
            List<Post> popularPosts = postMapper.selectPopularPosts(
                    postId,
                    limit - relatedPosts.size()
            );
            relatedPosts.addAll(popularPosts);
        }

        // 去重（避免重复添加同一篇文章）
        Set<Long> addedIds = new HashSet<>();
        List<Post> distinctPosts = new ArrayList<>();

        for (Post post : relatedPosts) {
            if (!addedIds.contains(post.getId())) {
                distinctPosts.add(post);
                addedIds.add(post.getId());
            }
            if (distinctPosts.size() >= limit) break;
        }

        return distinctPosts;
    }
}