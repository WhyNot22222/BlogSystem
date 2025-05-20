package com.yn.controller;

import com.yn.common.Result;
import com.yn.entity.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {

    // 新增获取头像接口
    @GetMapping("/getAvatar")
    public Result getAvatar(@RequestParam Long userId) {
        try {
            String uploadDir = "E:\\IdeaProjects\\DesignPatterns\\uploads\\avatars\\user_" + userId;
            Optional<Path> avatarFile = Files.list(Paths.get(uploadDir))
                .filter(path -> path.getFileName().toString().startsWith("avatar."))
                .findFirst();
    
            byte[] imageBytes;
            if (avatarFile.isEmpty()) {
                Resource defaultAvatar = new ClassPathResource("static/default.jpg");
                imageBytes = StreamUtils.copyToByteArray(defaultAvatar.getInputStream());
            } else {
                Resource imageResource = new UrlResource(avatarFile.get().toUri());
                imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
            }
            
            // 将图片数据转换为 Base64 字符串
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return Result.success(base64Image);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId) {  // 新增用户ID参数
        try {
            String projectRoot = System.getProperty("user.dir");
            // 按用户ID创建独立目录
            String userDir = "user_" + userId;
            String uploadDir = projectRoot + "/uploads/avatars/" + userDir + "/";
            
            File storageDir = new File(uploadDir);
            if (!storageDir.exists()) {
                System.out.println("创建用户目录: " + storageDir.mkdirs());
            }

            // 固定文件名格式：avatar.ext
            String fileExtension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = "avatar" + fileExtension; 
            String filePath = uploadDir + fileName;
            
            File dest = new File(filePath);
            if (dest.exists()) {
                Files.delete(dest.toPath()); // 先删除已存在的文件
            }
            file.transferTo(dest);
            
            return Result.success("/avatar/" + userDir + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

}