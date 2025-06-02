package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.User;
import com.yn.service.FollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;

    @PostMapping("/add")
    public Result follow(
            @RequestParam Long followerId,
            @RequestParam Long followedId
    ) {
        boolean success = followService.follow(followerId, followedId);
        return success ?
                Result.success("关注成功") :
                Result.error("已关注该用户");
    }

    @DeleteMapping("/unfollow")
    public Result unfollow(
            @RequestParam("followerId") Long followerId,
            @RequestParam("followedId") Long followedId) {
        boolean success = followService.unfollow(followerId, followedId);
        return success ?
                Result.success("取消成功") :
                Result.error("关注记录不存在");
    }

    @GetMapping("/check")
    public Result checkFollow(
            @RequestParam("followerId") Long followerId,
            @RequestParam("followedId") Long followedId
    ) {
        boolean isFollowing = followService.isFollowing(followerId, followedId);
        return Result.success(isFollowing);
    }

    @GetMapping("/list-following")
    public Result listFollowing(@RequestParam Long followerId) {
        List<User> followingList = followService.listFollowing(followerId);
        return Result.success(followingList);
    }
}