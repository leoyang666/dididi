package com.codingforhappy.model;


/**
 * 乘客发起打车请求
 */
public class NewTripOrder {
    private String token; //乘客token
    private String startingDesc; //出发地描述，如宁波市鄞州区江南路1689号
    private Point startingPos; //出发地坐标
    private String destDesc; //目的地描述
    private Point destPos; //目的地坐标

    public NewTripOrder() {
    }

    public NewTripOrder(String token, String startingDesc, Point startingPos, String destDesc, Point destPos) {
        this.token = token;
        this.startingDesc = startingDesc;
        this.startingPos = startingPos;
        this.destDesc = destDesc;
        this.destPos = destPos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStartingDesc() {
        return startingDesc;
    }

    public void setStartingDesc(String startingDesc) {
        this.startingDesc = startingDesc;
    }

    public Point getStartingPos() {
        return startingPos;
    }

    public void setStartingPos(Point startingPos) {
        this.startingPos = startingPos;
    }

    public String getDestDesc() {
        return destDesc;
    }

    public void setDestDesc(String destDesc) {
        this.destDesc = destDesc;
    }

    public Point getDestPos() {
        return destPos;
    }

    public void setDestPos(Point destPos) {
        this.destPos = destPos;
    }
}
