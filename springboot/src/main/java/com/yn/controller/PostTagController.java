package com.yn.controller;

import com.yn.common.Result;
import com.yn.service.PostTagService;
import com.yn.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-tags")
public class PostTagController {
    @Resource
    private PostTagService postTagService;
    @Resource
    private TagService tagService;

    @PostMapping
    public Result addTags(
            @RequestParam Long postId,
            @RequestParam List<Long> tagIds) {
        postTagService.addTagsToPost(postId, tagIds);
        return Result.success("标签添加成功");
    }

    @GetMapping
    public Result getTags(@RequestParam Long postId) {
        return Result.success(postTagService.getTagsByPostId(postId));
    }

    @GetMapping("/name")
    public Result getTagNames(@RequestParam Long postId) {
        // 先获取标签ID列表
        List<Long> tagIds = postTagService.getTagsByPostId(postId);
        
        // 根据标签ID获取标签名称
        List<String> tagNames = tagService.getTagNamesByIds(tagIds);
        
        return Result.success(tagNames);
    }

    @DeleteMapping
    public Result clearTags(@RequestParam Long postId) {
        postTagService.clearPostTags(postId);
        return Result.success("标签已清空");
    }

    @DeleteMapping("/batch")
    public Result deleteByPostIdAndTagIds(
        @RequestParam Long postId,
        @RequestParam List<Long> tagIds) {
        postTagService.deleteByPostIdAndTagIds(postId, tagIds);
        return Result.success("标签关联已删除");
    }
}