package com.yn.service.strategy;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

// 抽象基类，封装公共逻辑
public abstract class AbstractFileStrategy implements FileStrategy {

    @Override
    public String upload(MultipartFile file, Long... ids) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        String uploadDir = getUploadDir(projectRoot, ids);

        File storageDir = new File(uploadDir);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = getFileNamePrefix() + fileExtension;
        String filePath = uploadDir + '/' + fileName;

        File dest = new File(filePath);
        if (dest.exists()) {
            Files.delete(dest.toPath());
        }
        file.transferTo(dest);

        return getRelativePath(ids, fileName);
    }

    @Override
    public String get(Long... ids) throws IOException {
        String uploadDir = getAbsoluteUploadDir(ids);
        Optional<Path> file = findFirstFileWithPrefix(uploadDir, getFileNamePrefix());

        if (file.isEmpty()) {
            return null;
        }

        return encodeFileToBase64(file.get());
    }

    protected abstract String getUploadDir(String projectRoot, Long... ids);

    protected abstract String getAbsoluteUploadDir(Long... ids);

    protected abstract String getFileNamePrefix();

    protected abstract String getRelativePath(Long[] ids, String fileName);

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
        byte[] imageBytes = org.springframework.util.StreamUtils.copyToByteArray(imageResource.getInputStream());
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}