package com.yn.service;

import com.yn.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {
    private static EmailService instance;    // 静态私有成员变量（单例）
    private JavaMailSender mailSender;
    private final String fromAddress = "2993946158@qq.com";

    // 私有构造函数确保单例，防止外部实例化
    private EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 全局访问点（线程安全）
    public static synchronized EmailService getInstance(JavaMailSender mailSender) {
        if (instance == null) {
            synchronized (EmailService.class) {
                if (instance == null) {
                    instance = new EmailService(mailSender);
                }
            }
        }
        return instance;
    }

    /**
     * 创建基础邮件模板（复用公共配置）
     */
    private SimpleMailMessage createBaseMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        return message;
    }

    /**
     * 发送欢迎邮件（使用模板方法）
     */
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = createBaseMessage();
        message.setTo(user.getEmail());
        message.setSubject("欢迎加入MyBLOG社区!");

        String content = String.format(
                """
                亲爱的 %s,
                
                感谢您注册MyBLOG账号！
                
                您的账号信息：
                用户名: %s
                注册邮箱: %s
                
                立即开始您的创作之旅吧！
                
                祝您使用愉快，
                Handsome Promise WhyNot!""",
                user.getUsername(),
                user.getUsername(),
                user.getEmail()
        );

        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 发送密码重置邮件（完整实现）
     */
    public void sendPasswordResetEmail(String email, String code) {
        SimpleMailMessage message = createBaseMessage();
        message.setTo(email);
        message.setSubject("MyBLOG密码重置验证码");

        String content = String.format(
                """
                您的密码重置验证码是: %s
                
                验证码有效期为5分钟，请尽快完成验证。
                如果您没有请求重置密码，请忽略此邮件。
                
                Handsome WhyNot Promise""",
                code
        );

        message.setText(content);
        mailSender.send(message);
    }
}