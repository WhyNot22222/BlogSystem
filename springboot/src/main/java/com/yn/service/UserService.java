package com.yn.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yn.entity.FavoritesCollection;
import com.yn.entity.User;
import com.yn.mapper.UserMapper;
import com.yn.event.UserRegisteredEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;

import java.util.List;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;

    @Resource
    private EventPublisherService eventPublisherService;

    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    public PageInfo<User> selectPage(int pageNum, int pageSize, String keyword, String role) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectByCondition(keyword, role);
        return PageInfo.of(userList);
    }

    @Transactional
    public void register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已被注册");
        }
        if (userMapper.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        try {
            user.setRole("user");
            userMapper.insertUser(user);

            // 观察者模式主题通知点：发布用户注册事件
            eventPublisherService.publishUserRegisteredEvent(user);

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_username")) {
                throw new RuntimeException("用户名已被注册");
            } else if (e.getMessage().contains("uk_email")) {
                throw new RuntimeException("邮箱已被注册");
            }
            throw new RuntimeException("注册失败：" + e.getRootCause().getMessage());
        }
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    public void updateProfile(User user) {
        User originalUser = userMapper.findById(user.getId());

        if (!originalUser.getUsername().equals(user.getUsername()) &&
                userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已被使用");
        }

        if (!originalUser.getEmail().equals(user.getEmail()) &&
                userMapper.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        try {
            userMapper.updateUser(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_username")) {
                throw new RuntimeException("用户名已被使用");
            } else if (e.getMessage().contains("uk_email")) {
                throw new RuntimeException("邮箱已被注册");
            }
            throw new RuntimeException("资料更新失败：" + e.getMessage());
        }
    }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (!currentPassword.equals(user.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }
        userMapper.updatePassword(userId, newPassword);
    }

    public void updateEmail(Long userId, String newEmail) {
        if (userMapper.findByEmail(newEmail) != null) {
            throw new RuntimeException("邮箱已被使用");
        }
        userMapper.updateEmail(userId, newEmail);
    }

    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    public void deleteUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(userId);
    }

    public boolean checkPermission(Long userId, String requiredRole) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        return user.getRole().equals(requiredRole);
    }
}