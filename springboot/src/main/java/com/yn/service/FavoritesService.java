package com.yn.service;

import com.yn.entity.Favorites;
import com.yn.entity.FavoritesCollection;
import com.yn.mapper.FavoritesMapper;
import com.yn.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FavoritesService {
    @Resource
    private FavoritesMapper favoritesMapper;
    @Resource
    private UserMapper userMapper;

    public FavoritesCollection createCollection(FavoritesCollection collection) {
        if (userMapper.findById(collection.getUserId()) == null) {
            return null; // 用户不存在
        }
        favoritesMapper.insertCollection(collection);
        return collection;
    }

    public boolean addFavorite(Favorites favorite) {
        // 检查是否已收藏
        if (existsByPostAndUser(favorite.getPostId(), favorite.getUserId())) {
            return false;
        }
        favoritesMapper.insertFavorite(favorite);
        return true;
    }

    public boolean removeFavorite(Long postId, Long userId) {
        return favoritesMapper.deleteFavorite(postId, userId) > 0;
    }

    public List<FavoritesCollection> getByUserId(Long userId) {
        if (userMapper.findById(userId) == null) {
            return Collections.emptyList(); // 用户不存在返回空列表
        }
        return favoritesMapper.selectByUserId(userId);
    }

    public boolean existsByPostAndUser(Long postId, Long userId) {
        return favoritesMapper.existsByPostAndUser(postId, userId) > 0;
    }

    public int countByPostId(Long postId) {
        return favoritesMapper.countByPostId(postId);
    }

    public int countByCollectionId(Long collectionId) {
        return favoritesMapper.countCollectionByCollectionId(collectionId);
    }

    public FavoritesCollection getCollectionById(Long collectionId) {
        return favoritesMapper.selectCollectionById(collectionId);
    }
}