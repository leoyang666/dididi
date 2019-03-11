package com.codingforhappy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger extends CheckableUser {
    private int userId;
    private String phoneNumber;
    private String userName;
    private String passwd;
    private String balance;
    private String token;
    private Date createTime;
    private Date lastLoginTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("userId: ").append(userId).append("\r\n");
        sb.append("passwd: ").append(passwd).append("\r\n");
        sb.append("balance:").append(balance).append("\r\n");
        sb.append("token: ").append(token).append("\r\n");
        sb.append("createTime: ").append(createTime).append("\r\n");
        sb.append("lastLoginTime: ").append(lastLoginTime).append("\r\n");
        return sb.toString();
    }
}
