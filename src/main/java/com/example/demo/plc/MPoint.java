package com.example.demo.plc;

public class MPoint {
    private final int address;
    private final String name;

    public MPoint(int address, String name) {
        this.address = address;
        this.name = name;
    }

    public int getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}