package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Tag;
import com.yn.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Resource
    private TagService tagService;

    @PostMapping
    public Result createTag(@RequestBody String name) {
        try {
            Tag tag = new Tag();
            tag.setName(name);
            Long tagId = tagService.createTag(tag);
            return Result.success(tagId);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage()); // 处理名称重复异常
        } catch (Exception e) {
            return Result.error("标签创建失败"); // 简化其他异常信息
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteTag(@PathVariable Long id) {
        try {
            boolean success = tagService.deleteTag(id);
            return success ? Result.success(true) : Result.error("标签删除失败");
        } catch (Exception e) {
            return Result.error("删除操作异常: " + e.getMessage());
        }
    }

    @GetMapping("/selectAll")
    public Result getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error("获取标签列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result getTagByName(@RequestParam String name) {
        try {
            Tag tag = tagService.getTagByName(name);
            return tag != null ? Result.success(tag) : Result.error("标签不存在");
        } catch (Exception e) {
            return Result.error("标签查询失败: " + e.getMessage());
        }
    }
}
