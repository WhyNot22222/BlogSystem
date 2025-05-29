package com.yn.service;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
}