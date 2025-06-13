package com.yn.controller;

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

    /**
     * 获取所有已发布的博客文章
     */
    @GetMapping("/selectAll")
    public Result getPublishedPosts() {
        List<Post> posts = postService.getPublishedPosts();
        return Result.success(posts);
    }

    /**
     * 获取用户关注的所有博客文章
     */
    @GetMapping("/followed")
    public Result getFollowedPosts(
        @RequestParam String userIds) {
        List<Long> userIdList = Arrays.stream(userIds.split(","))
                                    .map(Long::valueOf)
                                    .collect(Collectors.toList());
        List<Post> posts = postService.getPostsByUserIds(userIdList);
        return Result.success(posts);
    }

    /**
     * 获取指定ID的博客文章的相关文章
     */
    @GetMapping("/related/{id}")
    public Result getRelatedPosts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int limit) {
        List<Post> relatedPosts = postService.getRelatedPosts(id, limit);
        return Result.success(relatedPosts);
    }

    /**
     * 获取博客的浏览量
     */
    @PutMapping("/{postId}/views")
    public Result incrementViews(@PathVariable Long postId) {
        postService.incrementViews(postId);
        return Result.success();
    }

    /**
     * 获取某个用户的所有博客文章
     */
    @GetMapping("/user-posts")
    public Result getPostsByUser(@RequestParam Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return !posts.isEmpty() ? 
            Result.success(posts) : 
            Result.error("该用户暂无文章");
    }
}