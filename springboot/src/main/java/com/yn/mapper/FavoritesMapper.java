package com.yn.mapper;

import com.yn.entity.Favorites;
import com.yn.entity.FavoritesCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavoritesMapper {
    void insertCollection(FavoritesCollection collection);
    List<FavoritesCollection> selectByUserId(Long userId);
    void insertFavorite(Favorites favorite);
    int deleteFavorite(@Param("postId") Long postId,
                        @Param("userId") Long userId);
    Long getCollectionOwnerId(@Param("collectionId") Long collectionId);
    int existsByPostAndUser(@Param("postId") Long postId,
                            @Param("userId") Long userId);
    int countByPostId(@Param("postId") Long postId);
    int countCollectionByCollectionId(@Param("collectionId") Long collectionId);
    FavoritesCollection selectCollectionById(@Param("collectionId") Long collectionId);
    List<Long> selectPostIdsByCollectionId(@Param("collectionId") Long collectionId);
    void updateCollectionName(@Param("collectionId") Long collectionId, 
                             @Param("newName") String newName);
    int deleteCollection(@Param("collectionId") Long collectionId, 
                    @Param("userId") Long userId);
}