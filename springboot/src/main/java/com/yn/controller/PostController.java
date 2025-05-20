package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Post;
import com.yn.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
        try {
            Post createdPost = postService.createPost(post);
            return Result.success(createdPost);
        } catch (Exception e) {
            System.out.println("创建文章异常：" + e.getMessage());
            e.printStackTrace();
            return Result.error("500", "创建文章失败：" + e.getMessage());
        }
    }

    /**
     * 更新已有的博客文章
     */
    @PutMapping("/{id}")
    public Result updatePost(@PathVariable Long id, @RequestBody Post post) {
        try {
            post.setId(id);
            Post updatedPost = postService.updatePost(post);
            return Result.success(updatedPost);
        } catch (Exception e) {
            return Result.error("500", "更新文章失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定ID的博客文章
     */
    @GetMapping("/{id}")
    public Result getPost(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            if (post != null) {
                return Result.success(post);
            } else {
                return Result.error("404", "文章未找到");
            }
        } catch (Exception e) {
            return Result.error("500", "获取文章失败：" + e.getMessage());
        }
    }
}
