package com.yn.mapper;

import com.yn.entity.User;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    void insertUser(User user);
    void updateUser(User user);
    void updatePassword(Long userId, String newPassword);
    void updateEmail(Long userId, String newEmail);
}
