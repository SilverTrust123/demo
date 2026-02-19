package com.example.demo.cam;

public class DetectedObjectDTO {

    private String className;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int footX;
    private int footY;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getFootX() {
        return footX;
    }

    public void setFootX(int footX) {
        this.footX = footX;
    }

    public int getFootY() {
        return footY;
    }

    public void setFootY(int footY) {
        this.footY = footY;
    }

}