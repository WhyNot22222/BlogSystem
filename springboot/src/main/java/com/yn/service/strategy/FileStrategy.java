package com.yn.service.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStrategy {
    String upload(MultipartFile file, Long... ids) throws IOException;
    String get(Long... ids) throws IOException;
}