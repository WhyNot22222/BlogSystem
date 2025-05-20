package com.yn.controller;

import com.github.pagehelper.PageInfo;
import com.yn.common.Result;
import com.yn.entity.LoginRequest;
import com.yn.entity.User;
import com.yn.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/selectAll")       // 完整请求路径：http://localhost:8080/user/selectAll
    public Result selectAll(){
        List<User> userList = userService.selectAll();
        return Result.success(userList);
    }

    /*
    * 分页查询
    * */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        Result result = userService.register(user);
        return result;
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        System.out.println("username:" + loginRequest.getUsername() + ", password: " + loginRequest.getPassword());
        User authenticatedUser = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (authenticatedUser != null) {
            return Result.success(authenticatedUser);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    // 更新用户信息
    @PutMapping("/updateProfile")
    public Result updateProfile(@RequestBody User user) {
        return userService.updateProfile(user);
    }

    // 修改密码
    @PostMapping("/updatePassword")
    public Result updatePassword(
            @RequestParam Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword
    ) {
        return userService.updatePassword(userId, currentPassword, newPassword);
    }

    // 修改邮箱
    @PostMapping("/updateEmail")
    public Result updateEmail(
            @RequestParam Long userId,
            @RequestParam String newEmail
    ) {
        return userService.updateEmail(userId, newEmail);
    }
}
