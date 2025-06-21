package com.yn.service.factory;

import com.yn.mapper.UserMapper;

public class UpdateValidatorFactory extends UserValidatorFactory {
    @Override
    public UserValidator createValidator(UserMapper mapper) {
        return new UpdateValidator(mapper);
    }
}