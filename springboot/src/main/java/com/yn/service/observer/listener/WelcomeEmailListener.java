package com.yn.service.observer.listener;

import com.yn.entity.User;
import com.yn.service.observer.event.UserRegisteredEvent;
import com.yn.service.EmailService;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 观察者模式实现 - 欢迎邮件监听器
 */
@Component
public class WelcomeEmailListener {

    @Resource
    private JavaMailSender mailSender;

    /**
     * 处理用户注册事件 - 异步发送欢迎邮件
     * @param event 用户注册事件
     */
    @EventListener
    @Async
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        EmailService emailService = EmailService.getInstance(mailSender);
        emailService.sendWelcomeEmail(user);
    }
}