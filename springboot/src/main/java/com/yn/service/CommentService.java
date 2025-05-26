package com.yn.service;

import com.yn.common.Result;
import com.yn.entity.Comment;
import com.yn.mapper.CommentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    @Resource
    private CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public Result addComment(Comment comment) {
        // 验证父评论是否存在
        if (comment.getParentId() != null && 
            commentMapper.findById(comment.getParentId()) == null) {
            return Result.error("父评论不存在");
        }
        commentMapper.insertComment(comment);
        return Result.success(comment);
    }

    public Result getPostComments(Long postId) {
        return Result.success(commentMapper.selectByPostId(postId));
    }

    @Transactional
    public Result deleteComment(Long id) {
        List<Long> deleteIds = commentMapper.findCommentTreeIds(id);
        
        if (deleteIds.isEmpty()) {
            return Result.error("评论不存在");
        }
        
        deleteIds.forEach(commentMapper::deleteById);
        
        return Result.success("成功删除" + deleteIds.size() + "条评论");
    }
}