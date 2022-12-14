package com.ateamgroup.stoolbe.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService  {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5 ;
    private static final int ATTEMPT_INCREMENT = 1 ;
    private LoadingCache<String,Integer> loginAttemptCache ;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        // validate from cache if username exist or not and remove it
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttamptCache(String username) throws ExecutionException  {
        int attempts = 0 ;
        attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        loginAttemptCache.put(username , attempts);

    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS ;
    }

}
