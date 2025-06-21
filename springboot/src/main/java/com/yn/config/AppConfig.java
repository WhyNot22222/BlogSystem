package com.yn.config;

import com.yn.service.CachedCommentLikeService;
import com.yn.service.CommentLikeService;
import com.yn.service.DbCommentLikeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public CommentLikeService commentLikeService(DbCommentLikeService dbService) {
        // 用缓存装饰器包装基础数据库服务
        return new CachedCommentLikeService(dbService);
    }
}