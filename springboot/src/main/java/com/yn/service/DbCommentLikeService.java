package com.yn.service;

import com.yn.entity.CommentLike;
import com.yn.mapper.CommentLikeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DbCommentLikeService implements CommentLikeService {
    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Override
    public boolean likeComment(Long commentId, Long userId) {
        if (isLiked(commentId, userId)) return false;
        CommentLike like = new CommentLike(commentId, userId);
        return commentLikeMapper.insert(like) > 0;
    }

    @Override
    public boolean cancelLike(Long commentId, Long userId) {
        return commentLikeMapper.delete(commentId, userId) > 0;
    }

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        return commentLikeMapper.exists(commentId, userId) > 0;
    }

    @Override
    public int getLikeCount(Long commentId) {
        return commentLikeMapper.countByCommentId(commentId);
    }
}