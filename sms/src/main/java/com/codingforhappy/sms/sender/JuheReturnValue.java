package com.codingforhappy.sms.sender;

public class JuheReturnValue {

    private String reason;

    private JuheReturnResultValue result;

    private int error_code;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public JuheReturnResultValue getResult() {
        return result;
    }

    public void setResult(JuheReturnResultValue result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public interface ERROR_CODE {
        int OK = 0;
        int SYSTEM_ERROR = 30001;
        int WRONG_PHONENUM = 205401;
        int SEND_TOO_FREQUENTLY = 205405;
    }

    public interface ERROR_MESSAGE {
        String OK = "OK";
        String SYSTEM_ERROR = "There is something wrong";
    }
}