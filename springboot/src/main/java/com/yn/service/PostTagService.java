package com.yn.service;

import com.yn.mapper.PostTagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostTagService {
    @Resource
    private PostTagMapper postTagMapper;

    public PostTagService(PostTagMapper postTagMapper) {
        this.postTagMapper = postTagMapper;
    }

    public void addTagsToPost(Long postId, List<Long> tagIds) {
        tagIds.forEach(tagId -> postTagMapper.insert(postId, tagId));
    }

    public List<Long> getTagsByPostId(Long postId) {
        return postTagMapper.findTagsByPostId(postId);
    }

    public void clearPostTags(Long postId) {
        postTagMapper.deleteByPostId(postId);
    }

    public void deleteByPostIdAndTagIds(Long postId, List<Long> tagIds) {
        postTagMapper.deleteByPostIdAndTagIds(postId, tagIds);
    }
}