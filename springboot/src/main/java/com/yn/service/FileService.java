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

    private final FileStrategy avatarStrategy;
    private final FileStrategy coverStrategy;

    public FileService() {
        this.avatarStrategy = new AvatarFileStrategy();
        this.coverStrategy = new CoverFileStrategy();
    }

    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        return avatarStrategy.upload(file, userId);
    }

    public String uploadCover(MultipartFile file, Long userId, Long postId) throws IOException {
        return coverStrategy.upload(file, userId, postId);
    }

    public String getAvatar(Long userId) throws IOException {
        return avatarStrategy.get(userId);
    }

    public String getCover(Long userId, Long postId) throws IOException {
        return coverStrategy.get(userId, postId);
    }
}