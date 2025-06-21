package com.yn.service.factory;

import com.yn.entity.User;
import com.yn.mapper.UserMapper;

public class UpdateValidator implements UserValidator {
    private final UserMapper userMapper;

    public UpdateValidator(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void validate(User newUser, User originalUser) {
        if (!originalUser.getUsername().equals(newUser.getUsername()) &&
                userMapper.findByUsername(newUser.getUsername()) != null)
            throw new RuntimeException("用户名已被使用");
        if (!originalUser.getEmail().equals(newUser.getEmail()) &&
                userMapper.findByEmail(newUser.getEmail()) != null)
            throw new RuntimeException("邮箱已被注册");
    }
}