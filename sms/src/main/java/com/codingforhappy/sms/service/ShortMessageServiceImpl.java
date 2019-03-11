package com.codingforhappy.sms.service;

import com.codingforhappy.sms.persistence.SMSPersistenceOperation;
import com.codingforhappy.sms.sender.ShortMessageSender;

import java.util.ResourceBundle;

// TODO:异常处理
public class ShortMessageServiceImpl implements ShortMessageService{

    private SMSPersistenceOperation persistenceOperation;

    private ShortMessageSender sender;

    private ResourceBundle smsProperties;

    private String smsSignature;

    private String registerTempID;

    public void setSmsSignature(String smsSignature) {
        this.smsSignature = smsSignature;
    }

    public void setRegisterTempID(String registerTempID) {
        this.registerTempID = registerTempID;
    }

    public ShortMessageServiceImpl(SMSPersistenceOperation persistenceOperation, ShortMessageSender shortMessageSender) {
        this.persistenceOperation = persistenceOperation;
        this.sender = shortMessageSender;
        smsProperties = ResourceBundle.getBundle("SMS");
    }

    @Override
    public SMSResponese sendVerificationCode(String phoneNum, String type) {

        SMSResponese response;

        // 验证该用户在该 method 下是否已存在验证码, 存在的话返回错误信息
        if (persistenceOperation.hasVerificationCode(type, phoneNum) != null){
            response = new SMSResponese(SMSResponese.Event.CODE_HAS_EXISTED, SMSResponese.Message.CODE_HAS_EXISTED);
            return response;
        }

        // 生成验证码
        String verificationCode = VerificationCodeGenerator.getRandNum(CODE_LENGTH);

        // 发送验证码
        // 为了便于扩展可以考虑一下策略模式
        if (type.equals(VerificationCodeType.REGISTER)){
            response = sender.sendVerificationCode(
                    phoneNum,
                    verificationCode,
                    smsSignature,
                    registerTempID);
        }
        else {
            response = new SMSResponese(
                    SMSResponese.Event.NOT_SUPPORTED_METHOD, SMSResponese.Message.NOT_SUPPORTED_METHOD);
        }

        // 如果发送成功，则将验证码存入 Redis 并设置超时时间
        if (response.getEvent().equals(SMSResponese.Event.OK)){
            persistenceOperation.addVerificationCode(type, phoneNum, verificationCode);
        }

        return response;
    }

    @Override
    public SMSResponese authenticateVerificationCode(String phoneNum, String verificationCode, String type) {

        String code;

        SMSResponese response;

        // 检测该项验证码是否存在
        code = persistenceOperation.hasVerificationCode(type, phoneNum);

        // 存在的话，说明验证成功，删除 persistence 中的对应验证码，并返回
        if (code != null && code.equals(verificationCode)){
            persistenceOperation.deleteRegisterVerificationCode(type, phoneNum);
            return new SMSResponese(SMSResponese.Event.OK, SMSResponese.Message.OK);
        }

        return  new SMSResponese(SMSResponese.Event.VERIFICATION_FAILED, SMSResponese.Message.VERIFICATION_FAILED);
    }
}
