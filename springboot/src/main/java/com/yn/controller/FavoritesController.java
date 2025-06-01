package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Favorites;
import com.yn.entity.FavoritesCollection;
import com.yn.entity.Post;
import com.yn.service.FavoritesService;
import com.yn.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {
    @Resource
    private FavoritesService favoritesService;
    @Resource
    private PostService postService;

    @PostMapping("/collection")
    public Result createCollection(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Boolean isPublic) {
        FavoritesCollection collection = new FavoritesCollection(userId, name, description, isPublic);
        FavoritesCollection created = favoritesService.createCollection(collection);
        return created != null ?
                Result.success(created) :
                Result.error("用户不存在");
    }

    @GetMapping("/collections")
    public Result getCollectionById(@RequestParam Long collectionId) {
        FavoritesCollection collection = favoritesService.getCollectionById(collectionId);
        return collection != null ?
                Result.success(collection) :
                Result.error("收藏夹不存在");
    }

    @PostMapping("/add")
    public Result addFavorite(@RequestBody Favorites favorite) {
        boolean success = favoritesService.addFavorite(favorite);
        return success ?
                Result.success("收藏成功") :
                Result.error("已收藏该内容");
    }

    @DeleteMapping("/remove")
    public Result removeFavorite(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean success = favoritesService.removeFavorite(postId, userId);
        return success ?
                Result.success("取消收藏成功") :
                Result.error("收藏记录不存在");
    }

    @GetMapping("/collections/list")
    public Result getCollections(@RequestParam Long userId) {
        List<FavoritesCollection> collections = favoritesService.getByUserId(userId);
        return Result.success(collections);
    }

    @GetMapping("/check")
    public Result checkFavoriteExists(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean exists = favoritesService.existsByPostAndUser(postId, userId);
        return Result.success(exists);
    }

    @GetMapping("/count")
    public Result getFavoritesCount(@RequestParam Long postId) {
        int count = favoritesService.countByPostId(postId);
        return Result.success(count);
    }

    @GetMapping("/collection/count")
    public Result getCollectionItemCount(@RequestParam Long collectionId) {
        int count = favoritesService.countByCollectionId(collectionId);
        return Result.success(count);
    }

    @GetMapping("/collection/posts")
    public Result getCollectionPosts(@RequestParam Long collectionId) {
        // 1. 获取收藏夹中的帖子ID列表
        List<Long> postIds = favoritesService.getPostIdsByCollectionId(collectionId);
        
        // 2. 通过PostService获取完整帖子数据
        List<Post> posts = postService.getPostsByIds(postIds);
        
        return Result.success(posts);
    }
}