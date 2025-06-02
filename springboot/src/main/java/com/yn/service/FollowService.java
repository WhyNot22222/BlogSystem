package com.yn.service;

import com.yn.entity.Follow;
import com.yn.entity.User;
import com.yn.mapper.FollowMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FollowService {
    @Resource
    private FollowMapper followMapper;
    @Resource
    private UserService userService;

    public boolean follow(Long followerId, Long followedId) {
        // 检查是否已关注
        if (isFollowing(followerId, followedId)) {
            return false;
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowedId(followedId);
        return followMapper.insert(follow) > 0;
    }

    public boolean unfollow(Long followerId, Long followedId) {
        return followMapper.delete(followerId, followedId) > 0;
    }

    public boolean isFollowing(Long followerId, Long followedId) {
        return followMapper.checkExists(followerId, followedId) > 0;
    }

    public List<User> listFollowing(Long followerId) {
        List<Long> followedIds = followMapper.selectFollowedIds(followerId);
        return followedIds.stream()
                .map(userService::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}