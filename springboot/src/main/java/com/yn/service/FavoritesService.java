package com.yn.service;

import com.yn.common.Result;
import com.yn.entity.Favorites;
import com.yn.entity.FavoritesCollection;
import com.yn.mapper.FavoritesMapper;
import com.yn.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesService {
    @Resource
    private FavoritesMapper favoritesMapper;
    @Resource
    private UserMapper userMapper;

    public Result createCollection(FavoritesCollection collection) {
        if (userMapper.findById(collection.getUserId()) == null) {
            return Result.error("用户不存在");
        }
        favoritesMapper.insertCollection(collection);
        return Result.success(collection);
    }

    public Result addFavorite(Favorites favorite) {
        // 验证收藏夹和用户权限
        favoritesMapper.insertFavorite(favorite);
        return Result.success("收藏成功");
    }

    public Result removeFavorite(Long postId, Long userId) {
        favoritesMapper.deleteFavorite(postId, userId);
        return Result.success("取消收藏成功");
    }

    public Result getByUserId(Long userId) {
        if (userMapper.findById(userId) == null) {
            return Result.error("用户不存在");
        }
        List<FavoritesCollection> collections = favoritesMapper.selectByUserId(userId);
        return Result.success(collections);
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