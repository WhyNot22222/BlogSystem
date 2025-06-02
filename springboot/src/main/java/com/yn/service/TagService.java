package com.yn.service;

import com.yn.entity.Tag;
import com.yn.mapper.TagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Resource
    private TagMapper tagMapper;

    public Long createTag(Tag tag) {
        if (tagMapper.selectByName(tag.getName()) != null) {
            return null;  // 名称重复时返回null
        }
        tagMapper.insert(tag);
        return tag.getId();
    }

    public Boolean deleteTag(Long id) {
        return tagMapper.deleteById(id) > 0;
    }

    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }

    public Tag getTagByName(String name) {
        return tagMapper.selectByName(name);
    }

    public Tag getTagById(Long id) {
        return tagMapper.selectById(id);
    }

    public List<Tag> getHotTags() {
        return tagMapper.selectHotTags();
    }

    public List<String> getTagNamesByIds(List<Long> tagIds) {
        return tagIds.stream()
                .map(tagId -> tagMapper.selectById(tagId))
                .filter(Objects::nonNull)
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}