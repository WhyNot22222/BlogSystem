package com.yn.service;

import com.yn.entity.Post;
import com.yn.mapper.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Resource
    private PostMapper postMapper;

    public Post createPost(Post post) {
        postMapper.insertPost(post);
        return post;
    }

    public Post updatePost(Post post) {
        postMapper.updatePost(post);
        return post;
    }

    public Post getPostById(Long id) {
        return postMapper.selectPostById(id);
    }
}
