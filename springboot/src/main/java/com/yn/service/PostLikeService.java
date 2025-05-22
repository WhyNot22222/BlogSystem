package com.yn.service;

import com.yn.entity.PostLike;
import com.yn.mapper.PostLikeMapper;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PostLikeService {
    @Resource
    private PostLikeMapper postLikeMapper;

    public boolean likePost(Long postId, Long userId) {
        try {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            return postLikeMapper.insert(like) > 0;
        } catch (DuplicateKeyException e) {
            return false; // 已点赞
        }
    }

    public boolean cancelLike(Long postId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userId", userId);
        return postLikeMapper.delete(params) > 0;
    }

    public boolean isLiked(Long postId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userId", userId);
        return postLikeMapper.exists(params) > 0;
    }
}