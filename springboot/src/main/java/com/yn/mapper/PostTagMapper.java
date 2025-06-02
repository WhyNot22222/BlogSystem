package com.yn.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PostTagMapper {
    int insert(Long postId, Long tagId);
    int deleteByPostId(Long postId);
    int deleteByTagId(Long tagId);
    List<Long> findTagsByPostId(Long postId);
    List<Long> findPostsByTagId(Long tagId);
    int deleteByPostIdAndTagIds(Long postId, List<Long> tagIds);
}