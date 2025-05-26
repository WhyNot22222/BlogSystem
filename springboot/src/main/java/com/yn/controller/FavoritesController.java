package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.Favorites;
import com.yn.entity.FavoritesCollection;
import com.yn.service.FavoritesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {
    @Resource
    private FavoritesService favoritesService;

    @PostMapping("/collection")
    public Result createCollection(@RequestBody FavoritesCollection collection) {
        return favoritesService.createCollection(collection);
    }

    @GetMapping("/collections")
    public Result getCollectionById(@RequestParam Long collectionId) {
        FavoritesCollection collection = favoritesService.getCollectionById(collectionId);
        return Result.success(collection);
    }

    @PostMapping("/add")
    public Result addFavorite(@RequestBody Favorites favorite) {
        return favoritesService.addFavorite(favorite);
    }

    @DeleteMapping("/remove")
    public Result removeFavorite(
        @RequestParam Long postId,
        @RequestParam Long userId) {
        return favoritesService.removeFavorite(postId, userId);
    }

    @GetMapping("/collections/list")
    public Result getCollections(@RequestParam Long userId) {
        return favoritesService.getByUserId(userId);
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
        return Result.success(favoritesService.countByCollectionId(collectionId));
    }
}