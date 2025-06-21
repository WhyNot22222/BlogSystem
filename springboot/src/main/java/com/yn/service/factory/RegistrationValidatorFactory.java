package com.yn.service.factory;

import com.yn.mapper.UserMapper;

public class RegistrationValidatorFactory extends UserValidatorFactory {
    @Override
    public UserValidator createValidator(UserMapper mapper) {
        return new RegistrationValidator(mapper);
    }
}