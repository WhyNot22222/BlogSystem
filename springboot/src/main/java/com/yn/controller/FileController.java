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

    @GetMapping("/getAvatar")
    public Result getAvatar(@RequestParam Long userId) {
        try {
            String uploadDir = "E:\\IdeaProjects\\DesignPatterns\\uploads\\avatars\\user_" + userId;
            Optional<Path> avatarFile = Files.list(Paths.get(uploadDir))
                .filter(path -> path.getFileName().toString().startsWith("avatar."))
                .findFirst();
    
            if (avatarFile.isEmpty()) {
                return Result.error("未找到用户头像");
            }
    
            Resource imageResource = new UrlResource(avatarFile.get().toUri());
            byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
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
            @RequestParam Long userId) {
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

    @PostMapping("/uploadCover")
    public Result uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long userId, // 允许空值
            @RequestParam(required = false) Long postId) { 
        try {
            // 添加参数校验
            if (userId == null) {
                return Result.error("用户ID不能为空");
            }
            if (postId == null) {
                return Result.error("文章ID不能为空");
            }
            String projectRoot = System.getProperty("user.dir");
            // 按用户和文章ID创建目录结构
            String storagePath = String.format("uploads/user_%d/post_%d/covers", userId, postId);
            String uploadDir = projectRoot + "/" + storagePath;
            
            File storageDir = new File(uploadDir);
            if (!storageDir.exists()) {
                System.out.println("创建封面目录: " + storageDir.mkdirs());
            }

            // 固定文件名格式：cover.ext
            String fileExtension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = "cover" + fileExtension;
            String filePath = uploadDir + '/' + fileName;
            
            File dest = new File(filePath);
            if (dest.exists()) {
                Files.delete(dest.toPath()); // 删除已存在的封面
            }
            file.transferTo(dest);
            
            return Result.success("/cover/user_" + userId + "/post_" + postId + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("封面上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/getCover")
    public Result getCover(
        @RequestParam Long userId, 
        @RequestParam Long postId) {
        try {
            String uploadDir = String.format("E:\\IdeaProjects\\DesignPatterns\\uploads\\user_%d\\post_%d\\covers", userId, postId);
            Optional<Path> coverFile = Files.list(Paths.get(uploadDir))
                    .filter(path -> path.getFileName().toString().startsWith("cover."))
                    .findFirst();
    
            if (coverFile.isEmpty()) {
                return Result.error("未找到文章封面");
            }
    
            Resource imageResource = new UrlResource(coverFile.get().toUri());
            byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return Result.success(base64Image);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("封面获取失败: " + e.getMessage());
        }
    }

}