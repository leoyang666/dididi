package com.codingforhappy.model.push;

import com.fasterxml.jackson.databind.ObjectMapper;


public class CustomMessage {
    private int command_type;

    //服务器发送给客户端的心跳包时，为SERVER
    //乘客和司机握手过程中，客户端收到的推送时，为对方用户
    private int account_type;

    //心跳包时，msg = "ping" or "pong"
    //订阅时，msg = token
    private String msg;

    public CustomMessage(int command_type, int account_type, String msg) {
        this.command_type = command_type;
        this.account_type = account_type;
        this.msg = msg;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public int getCommand_type() {
        return command_type;
    }

    public void setCommand_type(int command_type) {
        this.command_type = command_type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.err.println("not json");
        }
        return "{command_type: " + command_type +
                ", account_type: " + account_type +
                ", msg: " + msg + "}";
    }
}
