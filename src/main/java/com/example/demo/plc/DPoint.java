package com.example.demo.plc;

public class DPoint {
    private final int address;
    private final String name;

    public DPoint(int address, String name) {
        this.address = address;
        this.name = name;
    }

    public int getAddress() { return address; }
    public String getName() { return name; }
}