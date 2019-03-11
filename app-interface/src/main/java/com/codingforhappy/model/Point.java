package com.codingforhappy.model;

import java.io.Serializable;

/**
 * 用于序列化和反序列化的坐标，
 * 注意在redis里用org.springframework.data.geo.Point，而后端和前端交互的坐标用这个
 */
public class Point implements Serializable {
    private static final long serialVersionUID = 2317422242000L;

    private double x;
    private double y;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point another = (Point) obj;
            return (getX() == another.getX() && getY() == another.getY());
        }
        return false;
    }
}
