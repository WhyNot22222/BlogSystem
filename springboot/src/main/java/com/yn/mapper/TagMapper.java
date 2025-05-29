package com.yn.mapper;

import com.yn.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    int insert(Tag tag);
    int deleteById(Long id);
    List<Tag> selectAll();
    Tag selectByName(String name);
    Tag selectById(Long id);
}
