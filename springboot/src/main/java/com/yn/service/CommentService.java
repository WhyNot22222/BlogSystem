package com.yn.service;

import com.yn.entity.Comment;
import com.yn.mapper.CommentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CommentService {
    @Resource
    private CommentMapper commentMapper;

    public Comment addComment(Comment comment) {
        // 验证父评论是否存在
        if (comment.getParentId() != null &&
                commentMapper.findById(comment.getParentId()) == null) {
            return null; // 父评论不存在
        }
        commentMapper.insertComment(comment);
        return comment;
    }

    public List<Comment> getPostComments(Long postId) {
        return commentMapper.selectByPostId(postId);
    }

    @Transactional
    public int deleteComment(Long id) {
        List<Long> deleteIds = commentMapper.findCommentTreeIds(id);

        if (deleteIds.isEmpty()) {
            return 0; // 评论不存在
        }

        deleteIds.forEach(commentMapper::deleteById);
        return deleteIds.size(); // 返回删除的评论数量
    }
}