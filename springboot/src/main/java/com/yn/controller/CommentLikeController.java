package com.yn.controller;

import com.yn.common.Result;
import com.yn.service.CommentLikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comments/likes")
public class CommentLikeController {
    @Resource
    private CommentLikeService commentLikeService;

    @PostMapping
    public Result likeComment(@RequestParam Long commentId,
                              @RequestParam Long userId) {
        boolean success = commentLikeService.likeComment(commentId, userId);
        return success ? Result.success("点赞成功") : Result.error("重复点赞");
    }

    @DeleteMapping
    public Result cancelLike(@RequestParam Long commentId, 
                            @RequestParam Long userId) {
        boolean success = commentLikeService.cancelLike(commentId, userId);
        return success ? Result.success("取消成功") : Result.error("取消失败");
    }

    @GetMapping("/check")
    public Result checkLikeStatus(@RequestParam Long commentId,
                                @RequestParam Long userId) {
        return Result.success(commentLikeService.isLiked(commentId, userId));
    }

    @GetMapping("/count/{commentId}")
    public Result getLikeCount(@PathVariable Long commentId) {
        return Result.success(commentLikeService.getLikeCount(commentId));
    }
}