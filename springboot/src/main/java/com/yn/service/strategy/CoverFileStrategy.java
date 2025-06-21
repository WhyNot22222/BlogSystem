package com.yn.service.strategy;

public class CoverFileStrategy extends AbstractFileStrategy {

    @Override
    protected String getUploadDir(String projectRoot, Long... ids) {
        Long userId = ids[0];
        Long postId = ids[1];
        String storagePath = String.format("uploads/user_%d/post_%d/covers", userId, postId);
        return projectRoot + "/" + storagePath;
    }

    @Override
    protected String getAbsoluteUploadDir(Long... ids) {
        Long userId = ids[0];
        Long postId = ids[1];
        return String.format("E:\\IdeaProjects\\DesignPatterns\\uploads\\user_%d\\post_%d\\covers", userId, postId);
    }

    @Override
    protected String getFileNamePrefix() {
        return "cover";
    }

    @Override
    protected String getRelativePath(Long[] ids, String fileName) {
        Long userId = ids[0];
        Long postId = ids[1];
        return "/cover/user_" + userId + "/post_" + postId + "/" + fileName;
    }
}