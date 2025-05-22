package com.yn.mapper;

import com.yn.entity.PostLike;
import java.util.Map;

public interface PostLikeMapper {
    int insert(PostLike postLike);
    int delete(Map<String, Object> params);
    int exists(Map<String, Object> params);
}