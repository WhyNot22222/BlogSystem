package com.yn.service.factory;

import com.yn.entity.User;
import com.yn.mapper.UserMapper;

public class RegistrationValidator implements UserValidator {
    private final UserMapper userMapper;

    public RegistrationValidator(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void validate(User newUser, User originalUser) {
        if (userMapper.findByUsername(newUser.getUsername()) != null)
            throw new RuntimeException("用户名已被注册");
        if (userMapper.findByEmail(newUser.getEmail()) != null)
            throw new RuntimeException("邮箱已被注册");
    }
}