package com.yn.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yn.common.Result;
import com.yn.entity.FavoritesCollection;
import com.yn.entity.User;
import com.yn.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;

    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll();
        return PageInfo.of(userList);
    }

    @Resource
    private FavoritesService favoritesService;

    public Result register(User user) {
        try {
            if (userMapper.findByUsername(user.getUsername()) != null) {
                return Result.error("用户名已被注册");
            }
            if (userMapper.findByEmail(user.getEmail()) != null) {
                return Result.error("邮箱已被注册");
            }
            userMapper.insertUser(user);
            
            // 创建默认收藏夹
            FavoritesCollection defaultCollection = new FavoritesCollection();
            defaultCollection.setUserId(user.getId());
            defaultCollection.setName("默认收藏夹");
            favoritesService.createCollection(defaultCollection);

            return Result.success("注册成功");
        } catch (DataIntegrityViolationException e) {
            // 处理数据库唯一约束异常
            if (e.getMessage().contains("uk_username")) {
                return Result.error("用户名已被注册");
            } else if (e.getMessage().contains("uk_email")) {
                return Result.error("邮箱已被注册");
            }
            return Result.error("注册失败：" + e.getRootCause().getMessage());
        } catch (Exception e) {
            return Result.error("系统异常：" + e.getMessage());
        }
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user; // 返回完整的用户对象
        }
        return null;
    }

    // 更新用户基本信息
    public Result updateProfile(User user) {
        try {
            // 获取当前用户原始数据
            User originalUser = userMapper.findById(user.getId());
            
            // 用户名修改校验
            if (!originalUser.getUsername().equals(user.getUsername())) {
                if (userMapper.findByUsername(user.getUsername()) != null) {
                    return Result.error("用户名已被使用");
                }
            }
            
            // 邮箱修改校验
            if (!originalUser.getEmail().equals(user.getEmail())) {
                if (userMapper.findByEmail(user.getEmail()) != null) {
                    return Result.error("邮箱已被注册");
                }
            }
            
            userMapper.updateUser(user);
            return Result.success("资料更新成功");
        } catch (DataIntegrityViolationException e) {
            // 处理并发情况下的唯一约束冲突
            if (e.getMessage().contains("uk_username")) {
                return Result.error("用户名已被使用");
            } else if (e.getMessage().contains("uk_email")) {
                return Result.error("邮箱已被注册");
            }
            return Result.error("资料更新失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统异常：" + e.getMessage());
        }
    }

    // 修改密码（需验证旧密码）
    public Result updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (!currentPassword.equals(user.getPassword())) {
            return Result.error("当前密码错误");
        }
        userMapper.updatePassword(userId, newPassword);
        return Result.success("密码修改成功");
    }

    // 修改邮箱（需验证新邮箱唯一性）
    public Result updateEmail(Long userId, String newEmail) {
        if (userMapper.findByEmail(newEmail) != null) {
            return Result.error("邮箱已被使用");
        }
        userMapper.updateEmail(userId, newEmail);
        return Result.success("邮箱更新成功");
    }

    // 根据用户ID查询用户信息
    public Result findById(Long userId) {
        System.out.println("查询用户：" + userId);
        User user = userMapper.findById(userId);
        if (user!= null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
}
