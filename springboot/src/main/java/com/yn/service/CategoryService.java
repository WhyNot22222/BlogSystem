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

    public int addCategory(Category category) {
        return categoryMapper.insertCategory(category);
    }

    public int updateCategory(Category category) {
        return categoryMapper.updateCategory(category);
    }

    public int deleteCategory(Long id) {
        return categoryMapper.deleteCategory(id);
    }
}