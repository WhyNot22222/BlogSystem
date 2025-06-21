package com.yn.service;

public interface CommentLikeService {
    boolean likeComment(Long commentId, Long userId);
    boolean cancelLike(Long commentId, Long userId);
    boolean isLiked(Long commentId, Long userId);
    int getLikeCount(Long commentId);
}