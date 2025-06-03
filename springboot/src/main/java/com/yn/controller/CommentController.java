package com.yn.controller;

import com.github.pagehelper.PageInfo;
import com.yn.common.Result;
import com.yn.entity.Comment;
import com.yn.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(comment);
        return savedComment != null ?
                Result.success(savedComment) :
                Result.error("父评论不存在");
    }

    @GetMapping("/post")
    public Result getByPostId(@RequestParam Long postId) {
        List<Comment> comments = commentService.getPostComments(postId);
        return Result.success(comments);
    }

    @GetMapping("/user")
    public Result getByUserId(
        @RequestParam Long userId,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize) {
    
        PageInfo<Comment> comments = commentService.getUserComments(userId, keyword, status, startTime, endTime, page, pageSize);
        return Result.success(comments);
    }
    
    // 添加删除评论端点（原Service有此功能但Controller未暴露）
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Long id) {
        int deletedCount = commentService.deleteComment(id);
        return deletedCount > 0 ?
                Result.success("成功删除" + deletedCount + "条评论") :
                Result.error("评论不存在");
    }

    @PostMapping("/audit")
    public Result auditComment(@RequestParam Long id, @RequestParam String status) {
        int result = commentService.auditComment(id, status);
        return result > 0 ? 
            Result.success("审核状态更新成功") : 
            Result.error("审核操作失败");
    }
}