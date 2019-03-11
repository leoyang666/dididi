package com.codingforhappy.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * 成功匹配了司机和乘客，新行程开始，有关新行程的信息
 */
public class NewTripInfo implements Serializable {
    private static final long serialVersionUID = 32738917242000L;

    private DriverInfo driver;
    private PassengerInfo passenger;

    private long startTime; //毫秒

    private String startingDesc; //出发地描述，如宁波市鄞州区江南路1689号
    private Point startingPos; //出发地坐标
    private String destDesc; //目的地描述
    private Point destPos; //目的地坐标

    public NewTripInfo(NewTripOrder order) {
        this.startingDesc = order.getStartingDesc();
        this.startingPos = order.getStartingPos();
        this.destDesc = order.getDestDesc();
        this.destPos = order.getDestPos();

        this.startTime = System.currentTimeMillis();
    }

    public NewTripInfo() {
    }

    public DriverInfo getDriver() {
        return driver;
    }

    public void setDriver(DriverInfo driver) {
        this.driver = driver;
    }

    public PassengerInfo getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerInfo passenger) {
        this.passenger = passenger;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println("can't convert to json {}");
        }
        return jsonString;
    }
}
