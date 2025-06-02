package com.yn.mapper;

import com.yn.entity.Follow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FollowMapper {
    int insert(Follow follow);
    int delete(@Param("followerId") Long followerId,
               @Param("followedId") Long followedId);
    int checkExists(@Param("followerId") Long followerId,
                    @Param("followedId") Long followedId);
    List<Long> selectFollowedIds(@Param("followerId") Long followerId);
}