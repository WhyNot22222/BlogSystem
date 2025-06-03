package com.yn.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yn.entity.Comment;
import com.yn.mapper.CommentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public PageInfo<Comment> getUserComments(Long userId, String keyword, String status, String startTime, String endTime, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        List<Comment> comments = commentMapper.selectByCondition(params);
        return new PageInfo<>(comments);
    }

    public int auditComment(Long id, String status) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        return commentMapper.updateStatus(id, status);
    }
}