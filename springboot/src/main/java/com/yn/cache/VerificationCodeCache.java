package com.yn.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class VerificationCodeCache {
    // 存储验证码：key=邮箱, value=验证码
    private final Map<String, String> codeCache = new ConcurrentHashMap<>();

    // 存储已验证的邮箱
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void storeCode(String email, String code) {
        codeCache.put(email, code);
        // 5分钟后自动过期
        scheduler.schedule(() -> codeCache.remove(email), 5, TimeUnit.MINUTES);
    }

    public boolean isValidCode(String email, String code) {
        String storedCode = codeCache.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    public void markAsVerified(String email) {
        verifiedEmails.put(email, true);
        // 10分钟后自动过期
        scheduler.schedule(() -> verifiedEmails.remove(email), 10, TimeUnit.MINUTES);
    }

    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(verifiedEmails.get(email));
    }

    public void removeCode(String email) {
        codeCache.remove(email);
    }
}