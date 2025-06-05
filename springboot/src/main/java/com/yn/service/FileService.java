package com.yn.service;

import com.yn.service.strategy.AvatarFileStrategy;
import com.yn.service.strategy.CoverFileStrategy;
import com.yn.service.strategy.FileStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务类，处理文件上传和获取的业务逻辑
 */
@Service
public class FileService {

    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        FileStrategy avatarStrategy = new AvatarFileStrategy();
        return avatarStrategy.upload(file, userId);
    }

    public String uploadCover(MultipartFile file, Long userId, Long postId) throws IOException {
        FileStrategy coverStrategy = new CoverFileStrategy();
        return coverStrategy.upload(file, userId, postId);
    }

    public String getAvatar(Long userId) throws IOException {
        FileStrategy avatarStrategy = new AvatarFileStrategy();
        return avatarStrategy.get(userId);
    }

    public String getCover(Long userId, Long postId) throws IOException {
        FileStrategy coverStrategy = new CoverFileStrategy();
        return coverStrategy.get(userId, postId);
    }
}