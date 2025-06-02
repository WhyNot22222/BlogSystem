package com.yn.mapper;

import com.yn.entity.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {
    int insertPost(Post post);
    int updatePost(Post post);
    Post selectPostById(Long id);
    int deletePost(Long id);
    List<Post> selectPublishedPosts();
    List<Post> selectPostsByFollowerId(Long followerId);
    List<Post> selectPostsByUserIds(List<Long> userIds);
    // 查询相同分类的帖子
    List<Post> selectPostsByCategory(
            @Param("categoryId") Long categoryId,
            @Param("excludeId") Long excludeId,
            @Param("limit") int limit);

    // 查询帖子的标签ID
    List<Long> selectTagIdsByPostId(@Param("postId") Long postId);

    // 查询相同标签的帖子
    List<Post> selectPostsByTags(
            @Param("tagIds") List<Long> tagIds,
            @Param("excludeId") Long excludeId,
            @Param("limit") int limit);

    // 查询相同作者的帖子
    List<Post> selectPostsByAuthor(
            @Param("authorId") Long authorId,
            @Param("excludeId") Long excludeId,
            @Param("limit") int limit);

    // 查询热门帖子
    List<Post> selectPopularPosts(
            @Param("excludeId") Long excludeId,
            @Param("limit") int limit);

    List<Post> selectPostsByIds(@Param("postIds") List<Long> postIds);

    int incrementViews(@Param("postId") Long postId);

    List<Post> selectPostsByUserId(@Param("userId") Long userId);
}
