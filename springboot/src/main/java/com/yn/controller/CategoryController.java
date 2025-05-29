package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Category;
import com.yn.service.CategoryService;
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
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return category != null ?
                Result.success(category) :
                Result.error("分类未找到");
    }

    @PostMapping
    public Result createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        Category updated = categoryService.updateCategory(category);
        return updated != null ?
                Result.success(updated) :
                Result.error("分类更新失败，分类不存在");
    }

    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        return success ?
                Result.success() :
                Result.error("分类删除失败");
    }
}