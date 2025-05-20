package com.yn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地绝对路径到 `/avatar/**` URL
        String uploadDir = "E:\\IdeaProjects\\DesignPatterns\\uploads\\avatars";
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:" + uploadDir + File.separator);
    }
}