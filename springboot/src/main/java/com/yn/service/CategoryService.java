package com.yn.service;

import com.yn.entity.Category;
import com.yn.mapper.CategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectAllCategories();
    }

    public Category getCategoryById(Long id) {
        return categoryMapper.selectCategoryById(id);
    }

    public Category createCategory(Category category) {
        categoryMapper.insertCategory(category);
        return category;
    }

    public Category updateCategory(Category category) {
        int updatedRows = categoryMapper.updateCategory(category);
        return updatedRows > 0 ? category : null;
    }

    public boolean deleteCategory(Long id) {
        return categoryMapper.deleteCategory(id) > 0;
    }
}