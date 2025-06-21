package com.yn.service.factory;

import com.yn.mapper.UserMapper;

public abstract class UserValidatorFactory {
    public abstract UserValidator createValidator(UserMapper mapper);
}