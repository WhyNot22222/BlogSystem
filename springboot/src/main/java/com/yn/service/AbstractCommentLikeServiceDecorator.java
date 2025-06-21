package com.yn.service;

public abstract class AbstractCommentLikeServiceDecorator implements CommentLikeService {
    protected final CommentLikeService delegate;

    public AbstractCommentLikeServiceDecorator(CommentLikeService delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean likeComment(Long commentId, Long userId) {
        return delegate.likeComment(commentId, userId);
    }

    @Override
    public boolean cancelLike(Long commentId, Long userId) {
        return delegate.cancelLike(commentId, userId);
    }

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        return delegate.isLiked(commentId, userId);
    }

    @Override
    public int getLikeCount(Long commentId) {
        return delegate.getLikeCount(commentId);
    }
}