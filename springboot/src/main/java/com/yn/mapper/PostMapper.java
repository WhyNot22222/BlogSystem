package com.yn.mapper;

import com.yn.entity.Post;

public interface PostMapper {
    int insertPost(Post post);
    int updatePost(Post post);
    Post selectPostById(Long id);
    int deletePost(Long id);
}
