package com.codingforhappy.sms.service;

public interface ShortMessageService {

    interface VerificationCodeType{
        String REGISTER = "register_code";
    }

    short CODE_LENGTH = 6;

    SMSResponese sendVerificationCode(String phoneNum, String type);

    SMSResponese authenticateVerificationCode(String phoneNum, String verificationCode, String type);
}
