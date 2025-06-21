package com.yn.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CachedCommentLikeService extends AbstractCommentLikeServiceDecorator {
    private final ConcurrentMap<String, Boolean> likeStatusCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, AtomicInteger> likeCountCache = new ConcurrentHashMap<>();

    public CachedCommentLikeService(CommentLikeService delegate) {
        super(delegate);
    }

    @Override
    public boolean likeComment(Long commentId, Long userId) {
        String cacheKey = getCacheKey(commentId, userId);
        if (likeStatusCache.getOrDefault(cacheKey, false)) {
            return false; // 已点赞则跳过
        }

        boolean success = super.likeComment(commentId, userId);
        if (success) {
            likeStatusCache.put(cacheKey, true);
            likeCountCache.compute(commentId, (k, v) ->
                    v == null ? new AtomicInteger(1) : new AtomicInteger(v.incrementAndGet())
            );
        }
        return success;
    }

    @Override
    public boolean cancelLike(Long commentId, Long userId) {
        String cacheKey = getCacheKey(commentId, userId);
        if (!likeStatusCache.getOrDefault(cacheKey, false)) {
            return false; // 未点赞则跳过
        }

        boolean success = super.cancelLike(commentId, userId);
        if (success) {
            likeStatusCache.put(cacheKey, false);
            likeCountCache.computeIfPresent(commentId, (k, v) ->
                    v.decrementAndGet() == 0 ? null : v
            );
        }
        return success;
    }

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        String cacheKey = getCacheKey(commentId, userId);
        return likeStatusCache.computeIfAbsent(cacheKey,
                k -> super.isLiked(commentId, userId)
        );
    }

    @Override
    public int getLikeCount(Long commentId) {
        AtomicInteger count = likeCountCache.get(commentId);
        if (count == null) {
            int dbCount = super.getLikeCount(commentId);
            likeCountCache.put(commentId, new AtomicInteger(dbCount));
            return dbCount;
        }
        return count.get();
    }

    private String getCacheKey(Long commentId, Long userId) {
        return commentId + ":" + userId;
    }
}