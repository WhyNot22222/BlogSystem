package com.yn.mapper;

import com.yn.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommentMapper {
    void insertComment(Comment comment);
    List<Comment> selectByPostId(Long postId);
    Comment findById(Long id);
    int updateStatus(Long id, String status);
    void deleteById(Long id);
    List<Long> findCommentTreeIds(Long id);
    List<Comment> selectByUserId(@Param("userId") Long userId);
    List<Comment> selectByCondition(Map<String, Object> params);
}