package com.example.jwt5;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// DB를 나중에 붙이기 전까지는 Map 형태의 임시 저장소
@Component
public class RefreshTokenStore {

    // 간단하게 메모리 Map으로 저장
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public void saveRefreshToken(String username, String refreshToken) {
        refreshTokenStore.put(username, refreshToken);
    }

    public String getRefreshToken(String username) {
        return refreshTokenStore.get(username);
    }

    public void deleteRefreshToken(String username) {
        refreshTokenStore.remove(username);
    }
}
