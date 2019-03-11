package com.codingforhappy.sms.persistence;

public abstract class KeyUtils {

    public static String generateVerificationCodeKey(String type, String phoneNum){
        return type + ":" + phoneNum;
    }
}
