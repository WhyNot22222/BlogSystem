package com.yn.service.observer.event;

import com.yn.entity.User;

/**
 * 观察者模式事件对象
 * 封装用户注册事件相关数据，作为主题与观察者之间的通信载体
 */
public class UserRegisteredEvent {
    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}