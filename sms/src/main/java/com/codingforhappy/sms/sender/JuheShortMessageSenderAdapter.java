package com.codingforhappy.sms.sender;

import com.codingforhappy.sms.service.SMSResponese;

public class JuheShortMessageSenderAdapter implements ShortMessageSender {

    private String appKey;

    public JuheShortMessageSenderAdapter(String appkey) {
        this.appKey = appkey;
    }

    @Override
    public SMSResponese sendVerificationCode(String phoneNum, String code, String signature, String tempId) {

        JuheReturnValue returnValue = JuheSDK.send(
                phoneNum,
                tempId,
                "#code#="+code,
                appKey);

        SMSResponese response;
        switch (returnValue.getError_code()){
            case JuheReturnValue.ERROR_CODE.OK:
                response = new SMSResponese(SMSResponese.Event.OK, SMSResponese.Message.OK);
                break;
            case JuheReturnValue.ERROR_CODE.WRONG_PHONENUM:
                response = new SMSResponese(SMSResponese.Event.WRONG_PHONENUM, SMSResponese.Message.WRONG_PHONENUM);
                break;
            case JuheReturnValue.ERROR_CODE.SEND_TOO_FREQUENTLY:
                response = new SMSResponese(SMSResponese.Event.SEND_TOO_FREQUENTLY, SMSResponese.Message.SEND_TOO_FREQUENTLY);
                break;
            default:
                response = new SMSResponese(SMSResponese.Event.SYSTEM_ERROR, SMSResponese.Message.SYSTEM_ERROR);
                break;
        }

        return response;
    }
}
