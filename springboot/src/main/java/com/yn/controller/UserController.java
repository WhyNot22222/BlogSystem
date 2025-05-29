// UserController.java
package com.yn.controller;

import com.github.pagehelper.PageInfo;
import com.yn.common.Result;
import com.yn.entity.LoginRequest;
import com.yn.entity.User;
import com.yn.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        return Result.success(userService.selectAll());
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        User authenticatedUser = userService.login(
            loginRequest.getUsername(), 
            loginRequest.getPassword()
        );
        
        if (authenticatedUser != null) {
            return Result.success(authenticatedUser);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @PutMapping("/updateProfile")
    public Result updateProfile(@RequestBody User user) {
        try {
            userService.updateProfile(user);
            return Result.success("资料更新成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(
            @RequestParam Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword
    ) {
        try {
            userService.updatePassword(userId, currentPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updateEmail")
    public Result updateEmail(
            @RequestParam Long userId,
            @RequestParam String newEmail
    ) {
        try {
            userService.updateEmail(userId, newEmail);
            return Result.success("邮箱更新成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/getUser")
    public Result getUserById(@RequestParam Long userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
}