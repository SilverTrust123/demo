package com.example.demo.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sensor_border")
public class SensorBorder {
    @Id
    private String sensor_group;
    private int temp_1;
    private int temp_2;
    private int humi_1;
    private int humi_2;
    private int dust;
    private int qua;
    private int pow;
    private int timestamp;

    public SensorBorder() {

    }

    public void setTemp_1(int temp_1) {
        this.temp_1 = temp_1;
    }

    public int getTemp_1() {
        return temp_1;
    }

    public void setTemp_2(int temp_2) {
        this.temp_2 = temp_2;
    }

    public int getTemp_2() {
        return temp_2;
    }

    public void setHumi_1(int humi_1) {
        this.humi_1 = humi_1;
    }

    public int getHumi_1() {
        return humi_1;
    }

    public void setHumi_2(int humi_2) {
        this.humi_2 = humi_2;
    }

    public int getHumi_2() {
        return humi_2;
    }

    public String getSensor_group() {
        return sensor_group;
    }

    public void setSensor_group(String sensor_group) {
        this.sensor_group = sensor_group;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setDust(int dust) {
        this.dust = dust;
    }

    public int getDust() {
        return dust;
    }

    public void setQua(int qua) {
        this.qua = qua;
    }

    public int getQua() {
        return qua;
    }

    public void setPow(int pow) {
        this.pow = pow;
    }

    public int getPow() {
        return pow;
    }
}