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
    public Result createTag(@RequestBody String name) {
        Tag tag = new Tag();
        tag.setName(name);
        Long tagId = tagService.createTag(tag);
        return tagId != null ?
                Result.success(tagId) :
                Result.error("标签名称已存在");
    }

    @DeleteMapping("/{id}")
    public Result deleteTag(@PathVariable Long id) {
        boolean success = tagService.deleteTag(id);
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
}