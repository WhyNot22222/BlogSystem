package com.yn.controller;

import com.yn.common.Result;
import com.yn.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器，处理文件上传和获取的 HTTP 请求
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 获取用户头像
     * @param userId 用户 ID
     * @return 包含头像 Base64 编码字符串的结果对象，如果未找到头像则返回错误信息
     */
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

    /**
     * 上传用户头像
     * @param file 上传的头像文件
     * @param userId 用户 ID
     * @return 包含头像文件存储路径的结果对象，如果上传失败则返回错误信息
     */
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

    /**
     * 上传文章封面
     * @param file 上传的封面文件
     * @param userId 用户 ID
     * @param postId 文章 ID
     * @return 包含封面文件存储路径的结果对象，如果上传失败则返回错误信息
     */
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

    /**
     * 获取文章封面
     * @param userId 用户 ID
     * @param postId 文章 ID
     * @return 包含封面 Base64 编码字符串的结果对象，如果未找到封面则返回错误信息
     */
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