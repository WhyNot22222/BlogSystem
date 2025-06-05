package com.yn.mapper;

import com.yn.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAll();
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    void insertUser(User user);
    void updateUser(User user);
    void updatePassword(Long userId, String newPassword);
    void updateEmail(Long userId, String newEmail);
    void deleteById(Long userId);
    List<User> selectByCondition(@Param("keyword") String keyword, @Param("role") String role);
}