package com.yn.controller;

import com.yn.common.Result;
import com.yn.service.PostLikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class PostLikeController {
    @Resource
    private PostLikeService postLikeService;

    @PostMapping
    public Result likePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean success = postLikeService.likePost(postId, userId);
        return success ? Result.success("点赞成功") : Result.error("重复点赞");
    }

    @DeleteMapping
    public Result cancelLike(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        return postLikeService.cancelLike(postId, userId) 
            ? Result.success("取消成功") 
            : Result.error("取消失败");
    }

    @GetMapping("/check")
    public Result checkLikeStatus(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        return Result.success(postLikeService.isLiked(postId, userId));
    }

    @GetMapping("/count/{postId}")
    public Result getLikeCount(@PathVariable Long postId) {
        return Result.success(postLikeService.getLikeCount(postId));
    }
}