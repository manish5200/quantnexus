package com.quantnexus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private static final String LOGIN_PREFIX = "rate:login:";
    private static final String SIGNUP_PREFIX = "rate:register:";

    private final StringRedisTemplate redisTemplate;

    /**
     * DDoS Shield: Allows exactly 5 login attempts per minute per email.
     * Uses atomic incrementing in Redis to mathematically reject spam before touching the Database.
     */
    public boolean allowLoginAttempt(String email){
        String key = LOGIN_PREFIX + email;

        Long attempts = redisTemplate.opsForValue().increment(key);

        // If this is the absolute first strike, start the 60-second cooldown timer
        if(attempts != null && attempts == 1){
            redisTemplate.expire(key,1, TimeUnit.MINUTES);
        }

        if (attempts != null && attempts > 5) {
            log.warn("🛡️ Security Block: Login rate limit exceeded for email [{}]", email);
            return false;
        }

        return true;
    }


    /**
     * Prevents Botnet Fake-Account Flooding.
     * Limit: 3 attempts per hour per IP Address.
     */
    public boolean allowRegistrationAttempt(String ipAddress) {
        String key = "rate:register:" + ipAddress;
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }

        if (attempts != null && attempts > 3) {
            log.warn("🛡️ Spam Block: Registration rate limit exceeded for IP [{}]", ipAddress);
            return false;
        }
        return true;
    }



}
