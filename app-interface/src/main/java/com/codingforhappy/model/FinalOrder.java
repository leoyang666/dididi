package com.codingforhappy.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 行程结束时的订单，用于传回前端
 */
public class FinalOrder {
    private PassengerInfo passenger;
    private DriverInfo driver;

    private String startingDesc; //出发地描述，如宁波市鄞州区江南路1689号
    private Point startingPos; //出发地坐标
    private String destDesc; //目的地描述
    private Point destPos; //目的地坐标
    private String distance; //总路程，由前端发送

    private String price;

    private long startTime;
    private long endTime;

    public FinalOrder() {
    }

    public FinalOrder(NewTripInfo info) {
        this.passenger = info.getPassenger();
        this.driver = info.getDriver();

        this.startingDesc = info.getStartingDesc();
        this.startingPos = info.getStartingPos();
        this.destDesc = info.getDestDesc();
        this.destPos = info.getDestPos();

        this.startTime = info.getStartTime();
        this.endTime = System.currentTimeMillis();
    }

    public PassengerInfo getPassenger() {
        return passenger;
    }

    public FinalOrder setPassenger(PassengerInfo passenger) {
        this.passenger = passenger;
        return this;
    }

    public DriverInfo getDriver() {
        return driver;
    }

    public FinalOrder setDriver(DriverInfo driver) {
        this.driver = driver;
        return this;
    }

    public String getStartingDesc() {
        return startingDesc;
    }

    public FinalOrder setStartingDesc(String startingDesc) {
        this.startingDesc = startingDesc;
        return this;
    }

    public Point getStartingPos() {
        return startingPos;
    }

    public FinalOrder setStartingPos(Point startingPos) {
        this.startingPos = startingPos;
        return this;
    }

    public String getDestDesc() {
        return destDesc;
    }

    public FinalOrder setDestDesc(String destDesc) {
        this.destDesc = destDesc;
        return this;
    }

    public Point getDestPos() {
        return destPos;
    }

    public FinalOrder setDestPos(Point destPos) {
        this.destPos = destPos;
        return this;
    }

    public String getDistance() {
        return distance;
    }

    public FinalOrder setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public FinalOrder setPrice(String price) {
        this.price = price;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public FinalOrder setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public FinalOrder setEndTime(long endTime) {
        this.endTime = endTime;
        return this;
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
