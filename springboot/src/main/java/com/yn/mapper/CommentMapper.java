package com.yn.mapper;

import com.yn.entity.Comment;
import java.util.List;

public interface CommentMapper {
    void insertComment(Comment comment);
    List<Comment> selectByPostId(Long postId);
    int updateStatus(Long id, String status);
    Comment findById(Long id);
    void deleteById(Long id);
    List<Long> findCommentTreeIds(Long id);
}