package com.yn.mapper;

import com.yn.entity.Post;

import java.util.List;

public interface PostMapper {
    int insertPost(Post post);
    int updatePost(Post post);
    Post selectPostById(Long id);
    int deletePost(Long id);
    List<Post> selectPublishedPosts();
    List<Post> selectPostsByFollowerId(Long followerId);
    List<Post> selectPostsByUserIds(List<Long> userIds);
}
