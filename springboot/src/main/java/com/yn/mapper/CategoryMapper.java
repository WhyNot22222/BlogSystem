package com.yn.mapper;

import com.yn.entity.Category;

import java.util.List;

public interface CategoryMapper {
    int insertCategory(Category category);
    int updateCategory(Category category);
    Category selectCategoryById(Long id);
    int deleteCategory(Long id);
    List<Category> selectAllCategories();
}