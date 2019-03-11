package com.codingforhappy.sms.sender;

public class JuheReturnResultValue {
    private int count;

    private int fee;

    private String sid;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return Integer.toString(count) + "      " + Integer.toString(fee) + "   " + sid;
    }
}
