package com.yn.service.observer.listener;

import com.yn.entity.FavoritesCollection;
import com.yn.entity.User;
import com.yn.service.observer.event.UserRegisteredEvent;
import com.yn.service.FavoritesService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 观察者模式实现 - 事件监听器
 * 监听用户注册事件，自动创建默认收藏夹
 */
@Component
public class DefaultCollectionCreator {

    private final FavoritesService favoritesService;

    public DefaultCollectionCreator(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    /**
     * 观察者模式事件处理方法
     * @param event 用户注册事件（被观察的主题事件）
     */
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        FavoritesCollection defaultCollection = new FavoritesCollection();
        defaultCollection.setUserId(user.getId());
        defaultCollection.setName("默认收藏夹");
        favoritesService.createCollection(defaultCollection);
    }
}