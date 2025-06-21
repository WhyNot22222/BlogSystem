package com.yn.mapper;

import com.yn.entity.CommentLike;
import org.apache.ibatis.annotations.Param;

public interface CommentLikeMapper {
    int insert(CommentLike commentLike);
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int exists(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int countByCommentId(@Param("commentId") Long commentId);
}