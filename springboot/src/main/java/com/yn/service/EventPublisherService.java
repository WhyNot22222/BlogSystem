package com.yn.service;

import com.yn.entity.User;
import com.yn.service.observer.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 观察者模式实现 - 事件发布服务（主题角色）
 * 职责：封装Spring事件发布机制，提供领域事件发布接口
 */
@Service
public class EventPublisherService {
    // Spring事件发布器（被委托的实际发布者）
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 构造函数注入事件发布器
     * @param eventPublisher Spring框架提供的事件发布接口
     */
    public EventPublisherService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布用户注册领域事件
     * 观察者模式关键操作：通知所有监听该事件的观察者
     * @param user 新注册用户实体对象
     */
    public void publishUserRegisteredEvent(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(user);
        eventPublisher.publishEvent(event);
    }

    public void publishPasswordResetEvent(User user) {
        // 发个邮件通知用户密码重置了
    }
}