// UserController.java
package com.yn.controller;

import com.github.pagehelper.PageInfo;
import com.yn.common.Result;
import com.yn.entity.ForgotPasswordRequest;
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
    public Result selectPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize, keyword, role);
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

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("用户删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/auth/check-permission")
    public Result checkPermission(@RequestParam Long userId, @RequestParam String requiredRole) {
        try {
            boolean hasPermission = userService.checkPermission(userId, requiredRole);
            return Result.success(hasPermission);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 发送验证码
    @PostMapping("/sendVerificationCode")
    public Result sendVerificationCode(@RequestParam String email) {
        try {
            userService.sendVerificationCode(email);
            return Result.success("验证码发送成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 验证验证码
    @PostMapping("/verifyCode")
    public Result verifyCode(@RequestParam String email,
                             @RequestParam String code) {
        try {
            userService.verifyCode(email, code);
            return Result.success("验证码验证成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 重置密码
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return Result.success("密码重置成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}