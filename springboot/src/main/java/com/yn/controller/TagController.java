package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Tag;
import com.yn.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Resource
    private TagService tagService;

    @PostMapping
    public Result createTag(@RequestParam String name) {
        // 检查标签是否已存在
        Tag existingTag = tagService.getTagByName(name);
        if (existingTag != null) {
            return Result.success(existingTag.getId());
        }
        
        // 创建新标签
        Tag tag = new Tag();
        tag.setName(name);
        Long tagId = tagService.createTag(tag);
        return Result.success(tagId);
    }

    @DeleteMapping
    public Result deleteTag(@RequestParam Long tagId) {
        boolean success = tagService.deleteTag(tagId);
        return success ?
                Result.success(true) :
                Result.error("标签删除失败");
    }

    @GetMapping("/selectAll")
    public Result getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    @GetMapping("/search")
    public Result getTagByName(@RequestParam String name) {
        Tag tag = tagService.getTagByName(name);
        return tag != null ?
                Result.success(tag) :
                Result.error("标签不存在");
    }

    @GetMapping("/searchById")
    public Result getTagById(@RequestParam Long tagId) {
        Tag tag = tagService.getTagById(tagId);
        return tag!= null?
                Result.success(tag) :
                Result.error("标签不存在");
    }
}