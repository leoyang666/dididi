package com.codingforhappy.dao.redis;

import com.codingforhappy.sms.persistence.KeyUtils;
import com.codingforhappy.sms.persistence.SMSPersistenceOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository implements SMSPersistenceOperation {

    private final static long expireTime = 60;
    private final StringRedisTemplate template;
    private final ValueOperations<String, String> valueOperations;

    @Autowired
    public RedisRepository(StringRedisTemplate template) {
        this.template = template;
        valueOperations = template.opsForValue();
    }

    public String hasVerificationCode(String type, String phoneNum) {
        return valueOperations.get(KeyUtils.generateVerificationCodeKey(type, phoneNum));
    }

    public void addVerificationCode(String type, String phoneNum, String verificationCode) {
        valueOperations.set(KeyUtils.generateVerificationCodeKey(type, phoneNum), verificationCode, expireTime, TimeUnit.SECONDS);
    }

    public void deleteRegisterVerificationCode(String type, String phoneNum) {
        template.delete(KeyUtils.generateVerificationCodeKey(type, phoneNum));
    }
}