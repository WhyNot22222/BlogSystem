package com.yn.service.factory;

import com.yn.entity.User;

public interface UserValidator {
    void validate(User newUser, User originalUser) throws RuntimeException;
}