package com.yn.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yn.cache.VerificationCodeCache;
import com.yn.entity.ForgotPasswordRequest;
import com.yn.entity.User;
import com.yn.mapper.UserMapper;
import com.yn.service.factory.RegistrationValidatorFactory;
import com.yn.service.factory.UpdateValidatorFactory;
import com.yn.service.factory.UserValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private VerificationCodeCache verificationCodeCache;

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
        // 使用注册工厂创建校验器
        UserValidator validator = new RegistrationValidatorFactory().createValidator(userMapper);
        validator.validate(user, null);

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

        // 使用更新工厂创建校验器
        UserValidator validator = new UpdateValidatorFactory().createValidator(userMapper);
        validator.validate(user, originalUser);

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

    // ================== 忘记密码功能实现 ==================

    /**
     * 发送验证码到用户邮箱
     * @param email 用户邮箱
     */
    public void sendVerificationCode(String email) {
        // 检查邮箱是否注册
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }

        // 生成6位数字验证码
        String code = RandomStringUtils.randomNumeric(6);

        // 存储验证码到缓存
        verificationCodeCache.storeCode(email, code);

        // 发送验证码邮件
        EmailService emailService = EmailService.getInstance(mailSender);
        emailService.sendPasswordResetEmail(email, code);
    }

    /**
     * 验证用户输入的验证码
     * @param email 用户邮箱
     * @param code 验证码
     */
    public void verifyCode(String email, String code) {
        if (!verificationCodeCache.isValidCode(email, code)) {
            throw new RuntimeException("验证码无效或已过期");
        }
        // 标记邮箱为已验证状态
        verificationCodeCache.markAsVerified(email);
    }

    /**
     * 重置用户密码
     * @param request 重置密码请求
     */
    @Transactional
    public void resetPassword(ForgotPasswordRequest request) {
        // 检查邮箱是否已验证
        if (!verificationCodeCache.isVerified(request.getEmail())) {
            throw new RuntimeException("请先完成邮箱验证");
        }

        // 检查密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 查找用户
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        userMapper.updatePassword(user.getId(), request.getNewPassword());

        // 清除缓存中的验证码
        verificationCodeCache.removeCode(request.getEmail());

//        // 发布密码重置事件
//        eventPublisherService.publishPasswordResetEvent(user);
    }
}