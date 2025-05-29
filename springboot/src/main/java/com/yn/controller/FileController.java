package com.yn.controller;

import com.yn.common.Result;
import com.yn.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @GetMapping("/getAvatar")
    public Result getAvatar(@RequestParam Long userId) {
        try {
            String base64Image = fileService.getAvatar(userId);
            return base64Image != null ?
                    Result.success(base64Image) :
                    Result.error("未找到用户头像");
        } catch (Exception e) {
            return Result.error("头像获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId) {
        try {
            String filePath = fileService.uploadAvatar(file, userId);
            return Result.success(filePath);
        } catch (Exception e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/uploadCover")
    public Result uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId,
            @RequestParam Long postId) {
        try {
            if (userId == null) {
                return Result.error("用户ID不能为空");
            }
            if (postId == null) {
                return Result.error("文章ID不能为空");
            }

            String filePath = fileService.uploadCover(file, userId, postId);
            return Result.success(filePath);
        } catch (Exception e) {
            return Result.error("封面上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/getCover")
    public Result getCover(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        try {
            String base64Image = fileService.getCover(userId, postId);
            return base64Image != null ?
                    Result.success(base64Image) :
                    Result.error("未找到文章封面");
        } catch (Exception e) {
            return Result.error("封面获取失败: " + e.getMessage());
        }
    }
}