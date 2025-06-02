package com.yn.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Base64;

@Service
public class FileService {

    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        String userDir = "user_" + userId;
        String uploadDir = projectRoot + "/uploads/avatars/" + userDir + "/";

        File storageDir = new File(uploadDir);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = "avatar" + fileExtension;
        String filePath = uploadDir + fileName;

        File dest = new File(filePath);
        if (dest.exists()) {
            Files.delete(dest.toPath());
        }
        file.transferTo(dest);

        return "/avatar/" + userDir + "/" + fileName;
    }

    public String uploadCover(MultipartFile file, Long userId, Long postId) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        String storagePath = String.format("uploads/user_%d/post_%d/covers", userId, postId);
        String uploadDir = projectRoot + "/" + storagePath;

        File storageDir = new File(uploadDir);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = "cover" + fileExtension;
        String filePath = uploadDir + '/' + fileName;

        File dest = new File(filePath);
        if (dest.exists()) {
            Files.delete(dest.toPath());
        }
        file.transferTo(dest);

        return "/cover/user_" + userId + "/post_" + postId + "/" + fileName;
    }

    public String getAvatar(Long userId) throws IOException {
        String uploadDir = "E:\\IdeaProjects\\DesignPatterns\\uploads\\avatars\\user_" + userId;
        Optional<Path> avatarFile = findFirstFileWithPrefix(uploadDir, "avatar");

        if (avatarFile.isEmpty()) {
            return null;
        }

        return encodeFileToBase64(avatarFile.get());
    }

    public String getCover(Long userId, Long postId) throws IOException {
        String uploadDir = String.format("E:\\IdeaProjects\\DesignPatterns\\uploads\\user_%d\\post_%d\\covers", userId, postId);
        Optional<Path> coverFile = findFirstFileWithPrefix(uploadDir, "cover");

        if (coverFile.isEmpty()) {
            return null;
        }

        return encodeFileToBase64(coverFile.get());
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }

    private Optional<Path> findFirstFileWithPrefix(String directory, String prefix) throws IOException {
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath)) {
            return Optional.empty();
        }

        return Files.list(dirPath)
                .filter(path -> path.getFileName().toString().startsWith(prefix + "."))
                .findFirst();
    }

    private String encodeFileToBase64(Path filePath) throws IOException {
        Resource imageResource = new UrlResource(filePath.toUri());
        byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}