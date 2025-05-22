package com.yn.controller;

import com.yn.entity.Category;
import com.yn.service.CategoryService;
import com.yn.common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Result getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("500", "获取分类列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result createCategory(@RequestBody Category category) {
        try {
            int result = categoryService.addCategory(category);
            if (result > 0) {
                return Result.success(category);
            } else {
                return Result.error("500", "创建分类失败");
            }
        } catch (Exception e) {
            return Result.error("500", "创建分类失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            int result = categoryService.updateCategory(category);
            if (result > 0) {
                return Result.success(category);
            } else {
                return Result.error("500", "更新分类失败");
            }
        } catch (Exception e) {
            return Result.error("500", "更新分类失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        try {
            int result = categoryService.deleteCategory(id);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("500", "删除分类失败");
            }
        } catch (Exception e) {
            return Result.error("500", "删除分类失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category != null) {
                return Result.success(category);
            } else {
                return Result.error("404", "分类未找到");
            }
        } catch (Exception e) {
            return Result.error("500", "获取分类失败：" + e.getMessage());
        }
    }
}