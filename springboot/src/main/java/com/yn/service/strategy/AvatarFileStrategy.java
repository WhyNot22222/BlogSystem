package com.yn.service.strategy;

public class AvatarFileStrategy extends AbstractFileStrategy {

    @Override
    protected String getUploadDir(String projectRoot, Long... ids) {
        Long userId = ids[0];
        String userDir = "user_" + userId;
        return projectRoot + "/uploads/avatars/" + userDir + "/";
    }

    @Override
    protected String getAbsoluteUploadDir(Long... ids) {
        Long userId = ids[0];
        return "E:\\IdeaProjects\\DesignPatterns\\uploads\\avatars\\user_" + userId;
    }

    @Override
    protected String getFileNamePrefix() {
        return "avatar";
    }

    @Override
    protected String getRelativePath(Long[] ids, String fileName) {
        Long userId = ids[0];
        return "/avatar/user_" + userId + "/" + fileName;
    }
}