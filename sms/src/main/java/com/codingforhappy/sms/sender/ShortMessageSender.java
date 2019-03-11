package com.codingforhappy.sms.sender;

import com.codingforhappy.sms.service.SMSResponese;

public interface ShortMessageSender {
    SMSResponese sendVerificationCode(String phoneNum, String code, String signature, String tempId);
}
