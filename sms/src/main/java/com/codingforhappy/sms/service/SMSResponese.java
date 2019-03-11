package com.codingforhappy.sms.service;

public class SMSResponese {

    private String event;       // 返回码，200为成功
    private String message;     // 返回信息

    public SMSResponese(String event, String message) {
        this.event = event;
        this.message = message;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public interface Event {
        String OK = "200";
        String CODE_HAS_EXISTED = "600";
        String NOT_SUPPORTED_METHOD = "601";
        String WRONG_PHONENUM = "602";
        String SEND_TOO_FREQUENTLY = "603";
        String SYSTEM_ERROR = "604";
        String VERIFICATION_FAILED = "605";
    }

    public interface Message {
        String OK = "OK!";
        String CODE_HAS_EXISTED = "Code has existed!";
        String NOT_SUPPORTED_METHOD = "This kind of method hasn't supported!";
        String WRONG_PHONENUM = "The phone number is wrong!";
        String SEND_TOO_FREQUENTLY = "Send code too frequently!";
        String SYSTEM_ERROR = "There is something wrong!";
        String VERIFICATION_FAILED = "Verification failed!";
    }
}
