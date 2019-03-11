package com.codingforhappy.sms.persistence;

public interface SMSPersistenceOperation {

    String hasVerificationCode(String type, String phoneNum);

    void addVerificationCode(String type, String phoneNum, String verificationCode);

    void deleteRegisterVerificationCode(String type, String phoneNum);
}
