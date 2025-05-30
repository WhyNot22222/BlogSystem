package com.yn.controller;

import com.github.pagehelper.Page;
import com.yn.common.Result;
import com.yn.entity.Post;
import com.yn.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 创建新的博客文章
     */
    @PostMapping
    public Result createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return createdPost != null ?
                Result.success(createdPost) :
                Result.error("文章创建失败");
    }

    /**
     * 更新已有的博客文章
     */
    @PutMapping("/{id}")
    public Result updatePost(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        Post updatedPost = postService.updatePost(post);
        return updatedPost != null ?
                Result.success(updatedPost) :
                Result.error("文章更新失败，文章不存在");
    }

    /**
     * 获取指定ID的博客文章
     */
    @GetMapping("/{id}")
    public Result getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return post != null ?
                Result.success(post) :
                Result.error("文章未找到");
    }

    @GetMapping("/selectAll")
    public Result getPublishedPosts() {
        List<Post> posts = postService.getPublishedPosts();
        return Result.success(posts);
    }

    @GetMapping("/followed")
    public Result getFollowedPosts(
        @RequestParam String userIds) {
        List<Long> userIdList = Arrays.stream(userIds.split(","))
                                    .map(Long::valueOf)
                                    .collect(Collectors.toList());
        List<Post> posts = postService.getPostsByUserIds(userIdList);
        return Result.success(posts);
    }

    @GetMapping("/related/{id}")
    public Result getRelatedPosts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int limit) {
        List<Post> relatedPosts = postService.getRelatedPosts(id, limit);
        return Result.success(relatedPosts);
    }
}